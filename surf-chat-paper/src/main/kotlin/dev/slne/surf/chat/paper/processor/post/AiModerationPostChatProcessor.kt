package dev.slne.surf.chat.paper.processor.post

import com.github.shynixn.mccoroutine.folia.launch
import de.maxbossing.webhookbuilder.sendWebhook
import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.core.common.service.DeletionService
import dev.slne.surf.chat.core.paper.redisApi
import dev.slne.surf.chat.paper.ai.OpenAiService
import dev.slne.surf.chat.paper.ai.openAiService
import dev.slne.surf.chat.paper.config.aiModerationConfig
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.redis.event.TeamMessageRedisEvent
import dev.slne.surf.chat.paper.util.appendBotIcon
import dev.slne.surf.chat.paper.util.hasPermission
import dev.slne.surf.punish.api.common.punishment.PunishType
import dev.slne.surf.punish.api.common.user.PunishmentUser
import it.unimi.dsi.fastutil.objects.Object2DoubleMap
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import java.awt.Color
import java.net.URI
import java.time.OffsetDateTime
import java.util.*

object AiModerationPostChatProcessor : PostChatProcessor {
    override suspend fun process(messageContext: MessageContext) {
        if (messageContext.isCancelled) {
            return
        }

        if (!aiModerationConfig.enabled) {
            return
        }

        processMessage(messageContext.messageData)
    }

    private fun postWebhook(
        messageData: MessageData,
        classification: OpenAiService.ClassificationResult
    ) {
        val senderUuid = messageData.sender

        runCatching {
            sendWebhook(URI.create(aiModerationConfig.webhookUrl).toURL()) {
                name("Arty AI Moderation")
                avatar(aiModerationConfig.webhookAvatarUrl)
                embed {
                    thumbnail {
                        url("https://mc-heads.net/avatar/$senderUuid")
                    }
                    title("Chat Nachricht moderiert")

                    when (classification.action) {
                        OpenAiService.ClassificationAction.SILENT_FLAG -> {
                            content("Nachricht wurde als unangemessen markiert — bitte überprüfen und ggf. handeln")
                        }

                        OpenAiService.ClassificationAction.DELETE -> {
                            content("Die Chat Nachricht wurde gelöscht.")
                        }

                        OpenAiService.ClassificationAction.SEVERE -> {
                            if (aiModerationConfig.autoMuteEnabled) {
                                content(
                                    "Die Chat Nachricht wurde gelöscht und der Absender wurde für " +
                                        "${aiModerationConfig.autoMuteDurationDays.coerceAtLeast(1)} Tage " +
                                        "stumm geschaltet — bitte überprüfen"
                                )
                            } else {
                                content(
                                    "Die Chat Nachricht wurde gelöscht und als schwerwiegend markiert — " +
                                        "bitte zeitnah überprüfen"
                                )
                            }
                        }

                        else -> Unit
                    }

                    color(
                        when (classification.action) {
                            OpenAiService.ClassificationAction.SILENT_FLAG -> Color.YELLOW
                            OpenAiService.ClassificationAction.DELETE -> Color.RED
                            OpenAiService.ClassificationAction.SEVERE -> Color.MAGENTA
                            else -> Color.WHITE
                        }
                    )

                    field {
                        name("Nachricht")
                        value(messageData.plainMessage)
                        inline = false
                    }

                    field {
                        name("Auslösende Kategorien")
                        value(formatScores(classification.matchedScores))
                        inline = false
                    }

                    field {
                        name("Höchste Modellwerte")
                        value(formatScores(classification.categoryScores, limit = 5))
                        inline = false
                    }

                    field {
                        name("Sender")
                        value("[${nameOrUuid(senderUuid)}](${aiModerationConfig.userPanelPrefix}$senderUuid)")
                        inline = true
                    }

                    val receiverUuid = messageData.receiver
                    if (receiverUuid != null) {
                        field {
                            name("Receiver")
                            value("[${nameOrUuid(receiverUuid)}](${aiModerationConfig.userPanelPrefix}$receiverUuid)")
                            inline = true
                        }
                    }

                    field {
                        name("Server")
                        value(messageData.server)
                        inline = true
                    }

                    field {
                        name("Type")
                        value(messageData.type.value)
                        inline = true
                    }
                }
            }
        }.onFailure {
            plugin.logger.warning("Failed to send webhook for AI moderation!")
        }
    }

    private fun formatScores(
        scores: Object2DoubleMap<OpenAiService.Category>,
        limit: Int = Int.MAX_VALUE
    ): String = scores.object2DoubleEntrySet()
        .sortedByDescending { it.doubleValue }
        .take(limit)
        .joinToString("\n") { entry ->
            val scorePercent = entry.doubleValue * 100
            "- ${entry.key.name} (${"%.2f".format(scorePercent)} %)"
        }

    private fun nameOrUuid(uuid: UUID): String {
        return Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString()
    }

    suspend fun processMessage(messageData: MessageData) {
        if (messageData.sender.hasPermission(PermissionRegistry.BYPASS_FILTER)) {
            return
        }

        val plain = messageData.plainMessage
        val classification = openAiService.classifyChatMessage(plain, messageData.type.value)

        if (classification.action == OpenAiService.ClassificationAction.NONE) {
            return
        }

        val name = messageData.senderUser().lastKnownName ?: messageData.sender.toString()

        postWebhook(messageData, classification)

        redisApi.publishEvent(
            TeamMessageRedisEvent(
                buildText {
                    appendBotIcon()
                    info("Die Nachricht von ")
                    variableValue(name)
                    info(" wurde von der KI-Moderation markiert: ")
                    spacer("(${messageData.type.value}) ")

                    append {
                        text(plain.take(20), Colors.WHITE)
                        if (plain.length > 20) {
                            text("...", Colors.GRAY)
                        }

                        hoverEvent(buildText {
                            text(plain, Colors.WHITE)
                        })
                    }

                    if (classification.action == OpenAiService.ClassificationAction.SILENT_FLAG) {
                        append {
                            spacer(" [")
                            text("LÖSCHEN", Colors.RED)
                            spacer("]")
                            hoverEvent(
                                buildText {
                                    text("Klicke hier, um die Nachricht zu löschen", Colors.RED)
                                }
                            )
                            clickEvent(ClickEvent.callback { clicked ->
                                plugin.launch {
                                    val deleted = DeletionService.deleteMessage(
                                        messageData,
                                        deleter = clicked,
                                        deletionReason = "AI classification: ${classification.action.name}",
                                        notifyTeam = false
                                    )

                                    if (!deleted) {
                                        clicked.sendText {
                                            appendErrorPrefix()
                                            error("Die Nachricht konnte nicht gelöscht werden!")
                                        }
                                    }
                                }
                            })
                        }
                    }
                }
            )).await()

        when (classification.action) {
            OpenAiService.ClassificationAction.DELETE -> {
                deleteMessage(messageData, classification.action)
            }

            OpenAiService.ClassificationAction.SEVERE -> {
                deleteMessage(messageData, classification.action)

                if (aiModerationConfig.autoMuteEnabled) {
                    mutePlayer(messageData, classification)
                }
            }

            else -> Unit
        }
    }

    private suspend fun deleteMessage(
        messageData: MessageData,
        action: OpenAiService.ClassificationAction
    ) {
        DeletionService.deleteMessage(
            messageData,
            deletionReason = "AI classification: ${action.name}",
        )
    }

    private suspend fun mutePlayer(
        messageData: MessageData,
        classification: OpenAiService.ClassificationResult
    ) {
        val sender = messageData.sender
        val durationDays = aiModerationConfig.autoMuteDurationDays.coerceAtLeast(1)
        val note = buildString {
            append("[AI MODERATION] Schwerwiegendes Chatverhalten: [")
            val scores = classification.matchedScores.object2DoubleEntrySet()
                .sortedByDescending { it.doubleValue }

            for (entry in scores) {
                val scorePercent = entry.doubleValue * 100
                append("${entry.key.name}=${"%.2f".format(scorePercent)} %")
                if (entry != scores.last()) {
                    append(", ")
                }
            }

            append("]")
        }

        PunishmentUser.byUuid(sender)
            .punish(PunishType.MUTE.Expirable(OffsetDateTime.now().plusDays(durationDays)) {
                note(note)
            }, "Schwerwiegendes unangemessenes Chatverhalten")
    }
}
