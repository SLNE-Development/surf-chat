package dev.slne.surf.chat.bukkit.processor.post

import de.maxbossing.webhookbuilder.sendWebhook
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.bukkit.ai.OpenAiService
import dev.slne.surf.chat.bukkit.ai.openAiService
import dev.slne.surf.chat.bukkit.config.aiModerationConfig
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.redis.event.TeamMessageRedisEvent
import dev.slne.surf.chat.bukkit.redisApi
import dev.slne.surf.chat.bukkit.util.appendBotIcon
import dev.slne.surf.chat.bukkit.util.hasPermission
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import java.awt.Color
import java.net.URI
import java.util.*

object AiModerationPostChatProcessor : PostChatProcessor {
    override suspend fun process(messageContext: MessageContext) {
        if (messageContext.isCancelled) {
            return
        }

        if (!aiModerationConfig.enabled) {
            return
        }

        if (messageContext.messageData.sender.hasPermission(PermissionRegistry.BYPASS_FILTER)) {
            return
        }

        val plain = messageContext.messageData.plainMessage
        val classification = openAiService.resultCache.get(plain)

        if (classification.action == OpenAiService.ClassificationAction.NONE) {
            return
        }

        val name = messageContext.messageData.sender.name

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
                    append {
                        spacer(" [")
                        text("LÖSCHEN", Colors.RED)
                        spacer("]")
                        hoverEvent(
                            buildText {
                                text("Klicke hier, um die Nachricht zu löschen", Colors.RED)
                            }
                        )
                        clickEvent(ClickEvent.callback {
                            val signature = messageContext.messageData.signature

                            if (signature == null) {
                                it.sendText {
                                    appendErrorPrefix()
                                    error("Die Nachricht konnte nicht gelöscht werden!")
                                }
                                return@callback
                            }

                            Bukkit.getServer().deleteMessage(signature)
                        })
                    }
                }
            ))

        when (classification.action) {
            OpenAiService.ClassificationAction.DELETE -> {
                historyService.markDeleted(
                    messageContext.messageData.messageUuid,
                    "Automod (surf-chat)"
                )

                messageContext.messageData.signature?.let { Bukkit.getServer().deleteMessage(it) }
            }

            OpenAiService.ClassificationAction.MUTE -> {
                val sender = messageContext.messageData.sender
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

//                sender.punishmentManager.punish(
//                    PunishType.MUTE.Expirable(ZonedDateTime.now().plusDays(7))
//                        .withNote(note),
//                    "Unangemessenes Chat verhalten"
//                ) //TODO: Punish
            }

            else -> Unit
        }
    }

    private fun postWebhook(
        messageContext: MessageContext,
        classification: OpenAiService.ClassificationResult
    ) {
        val senderUuid = messageContext.messageData.sender.uuid

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

                    val receiverUuid = messageContext.messageData.receiver?.uuid
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