package dev.slne.surf.chat.paper.processor.post

import com.github.shynixn.mccoroutine.folia.launch
import de.maxbossing.webhookbuilder.sendWebhook
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.paper.ai.OpenAiService
import dev.slne.surf.chat.paper.ai.openAiService
import dev.slne.surf.chat.paper.config.aiModerationConfig
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.redis.event.TeamMessageRedisEvent
import dev.slne.surf.chat.paper.redisApi
import dev.slne.surf.chat.paper.util.appendBotIcon
import dev.slne.surf.chat.paper.util.hasPermission
import dev.slne.surf.chat.core.service.deletionService
import dev.slne.surf.punish.api.punishment.PunishType
import dev.slne.surf.punish.api.user.PunishmentUser
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
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

        val messageData = messageContext.messageData
        if (messageData.sender.hasPermission(PermissionRegistry.BYPASS_FILTER)) {
            return
        }

        val plain = messageData.plainMessage
        val classification = openAiService.classifyChatMessage(plain)

        if (classification.action == OpenAiService.ClassificationAction.NONE) {
            return
        }

        val name = messageData.senderUser().lastKnownName ?: messageData.sender.toString()

        postWebhook(messageContext, classification)

        redisApi.publishEvent(
            TeamMessageRedisEvent(
                buildText {
                    appendBotIcon()
                    info("Die Nachricht von ")
                    variableValue(name)
                    info(" wurde als bedrohlich eingestuft: ")

                    append {
                        text(plain.take(20), Colors.WHITE)
                        if (plain.length > 20) {
                            text("...", Colors.GRAY)
                        }

                        hoverEvent(buildText {
                            text(plain, Colors.WHITE)
                        })
                    }

                    if (classification.action != OpenAiService.ClassificationAction.DELETE) {
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
                                    val deleted = deletionService.deleteMessage(
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
                deletionService.deleteMessage(
                    messageData,
                    deletionReason = "AI classification: ${classification.action.name}",
                )
            }

            OpenAiService.ClassificationAction.MUTE -> {
                val sender = messageData.sender
                val note = buildString {
                    append("[AI MODERATION] Unangemessenes Chat verhalten: [")
                    val scores = classification.flaggedScores.object2DoubleEntrySet()
                        .sortedByDescending { it.doubleValue }

                    for (entry in scores) {
                        val category = entry.key
                        val scorePercent = entry.doubleValue * 100
                        append("${category.name}=${"%.2f".format(scorePercent)} %")
                        if (entry != scores.last()) {
                            append(", ")
                        }
                    }

                    append("]")
                }

                PunishmentUser.byUuid(sender)
                    .punish(PunishType.MUTE.Expirable(OffsetDateTime.now().plusDays(7)) {
                        note(note)
                    }, "Unangemessenes Chat verhalten")
            }

            else -> Unit
        }
    }

    private fun postWebhook(
        messageContext: MessageContext,
        classification: OpenAiService.ClassificationResult
    ) {
        val senderUuid = messageContext.messageData.sender

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

                        OpenAiService.ClassificationAction.MUTE -> {
                            content("Die Chat Nachricht wurde gelöscht und der Absender wurde für 7 Tage stumm geschaltet — bitte überprüfen")
                        }

                        else -> Unit
                    }

                    color(
                        when (classification.action) {
                            OpenAiService.ClassificationAction.SILENT_FLAG -> Color.YELLOW
                            OpenAiService.ClassificationAction.DELETE -> Color.RED
                            OpenAiService.ClassificationAction.MUTE -> Color.MAGENTA
                            else -> Color.WHITE
                        }
                    )

                    field {
                        name("Nachricht")
                        value(messageContext.messageData.plainMessage)
                        inline = false
                    }

                    field {
                        name("Kategorien")
                        value(buildString {
                            classification.flaggedScores.object2DoubleEntrySet()
                                .sortedByDescending { it.doubleValue }
                                .forEachIndexed { index, entry ->
                                    val category = entry.key
                                    val scorePercent = entry.doubleValue * 100
                                    append("- ${category.name} (${"%.2f".format(scorePercent)} %)")
                                    if (index != classification.flaggedScores.size - 1) {
                                        append("\n")
                                    }
                                }
                        })
                        inline = false
                    }

                    field {
                        name("Sender")
                        value("[${nameOrUuid(senderUuid)}](${aiModerationConfig.userPanelPrefix}$senderUuid)")
                        inline = true
                    }

                    val receiverUuid = messageContext.messageData.receiver
                    if (receiverUuid != null) {
                        field {
                            name("Receiver")
                            value("[${nameOrUuid(receiverUuid)}](${aiModerationConfig.userPanelPrefix}$receiverUuid)")
                            inline = true
                        }
                    }

                    field {
                        name("Server")
                        value(messageContext.messageData.server)
                        inline = true
                    }

                    field {
                        name("Type")
                        value(messageContext.messageData.type.name)
                        inline = true
                    }
                }
            }
        }.onFailure {
            plugin.logger.warning("Failed to send webhook for AI moderation!")
        }
    }

    private fun nameOrUuid(uuid: UUID): String {
        return Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString()
    }
}