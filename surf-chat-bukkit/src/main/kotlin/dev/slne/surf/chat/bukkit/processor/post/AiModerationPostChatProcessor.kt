package dev.slne.surf.chat.bukkit.processor.post

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.bukkit.ai.OpenAiService
import dev.slne.surf.chat.bukkit.ai.openAiService
import dev.slne.surf.chat.bukkit.config.aiModerationConfig
import dev.slne.surf.chat.bukkit.redis.event.TeamMessageRedisEvent
import dev.slne.surf.chat.bukkit.redisApi
import dev.slne.surf.chat.bukkit.util.appendBotIcon
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import me.binarywriter.discordwebhooks.data.Image
import me.binarywriter.discordwebhooks.data.Webhook
import org.bukkit.Bukkit
import java.awt.Color
import java.util.*

object AiModerationPostChatProcessor : PostChatProcessor {
    override suspend fun process(messageContext: MessageContext) {
        if (messageContext.isCancelled) {
            return
        }

        if (!aiModerationConfig.enabled) {
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
        val senderUuid = messageContext.messageData.sender

        val webhook = Webhook {
            username = "Arty AI Moderation"
            avatarUrl = aiModerationConfig.webhookAvatarUrl
            embed {
                image = Image("https://mc-heads.net/avatar/$senderUuid")

                title = "Chat Nachricht moderiert"
                when (classification.action) {
                    OpenAiService.ClassificationAction.SILENT_FLAG -> {
                        description =
                            "Nachricht wurde als unangemessen markiert — bitte überprüfen und ggf. handeln"
                    }

                    OpenAiService.ClassificationAction.DELETE -> {
                        description = "Die Chat Nachricht wurde gelöscht."
                    }

                    OpenAiService.ClassificationAction.MUTE -> {
                        description =
                            "Die Chat Nachricht wurde gelöscht und der Absender wurde für 7 Tage stumm geschaltet — bitte überprüfen"
                    }

                    else -> Unit
                }

                color = when (classification.action) {
                    OpenAiService.ClassificationAction.SILENT_FLAG -> Color.YELLOW
                    OpenAiService.ClassificationAction.DELETE -> Color.RED
                    OpenAiService.ClassificationAction.MUTE -> Color.MAGENTA
                    else -> Color.WHITE
                }

                field {
                    name = "Nachricht"
                    value = messageContext.messageData.plainMessage
                    inline = false
                }

                field {
                    name = "Kategorien"
                    value = buildString {
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
                    }
                }

                field {
                    name = "Sender"
                    value =
                        "[${nameOrUuid(senderUuid.uuid)}](${aiModerationConfig.userPanelPrefix}$senderUuid)"
                    inline = true
                }

                val receiverUuid = messageContext.messageData.receiver?.uuid
                if (receiverUuid != null) {
                    field {
                        name = "Receiver"
                        value =
                            "[${nameOrUuid(receiverUuid)}](${aiModerationConfig.userPanelPrefix}$receiverUuid)"
                        inline = true
                    }
                }

                field {
                    name = "Server"
                    value = messageContext.messageData.server.internalName
                    inline = true
                }

                val channel = messageContext.messageData.channel
                if (channel != null) {
                    field {
                        name = "Channel"
                        value = channel
                        inline = true
                    }
                }

                field {
                    name = "Type"
                    value = messageContext.messageData.type.name
                    inline = true
                }
            }
        }

        webhook.send(aiModerationConfig.webhookUrl)
    }

    private fun nameOrUuid(uuid: UUID): String {
        return Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString()
    }
}