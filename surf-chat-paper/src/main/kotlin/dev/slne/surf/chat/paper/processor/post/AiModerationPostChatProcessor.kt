package dev.slne.surf.chat.paper.processor.post

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.core.common.ai.OpenAiService
import dev.slne.surf.chat.core.common.ai.OpenAiService.ClassificationAction
import dev.slne.surf.chat.core.common.ai.OpenAiService.ClassificationResult
import dev.slne.surf.chat.core.common.netty.packet.serverbound.ServerboundMessageDeletePacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.history.ServerboundHistoryMarkDeletedPacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.message.ServerboundTeamMessagePacket
import dev.slne.surf.chat.paper.config.aiModerationConfig
import dev.slne.surf.chat.paper.util.appendBotIcon
import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.cloud.api.common.player.punishment.type.PunishType
import dev.slne.surf.cloud.api.common.player.toOfflineCloudPlayer
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import me.binarywriter.discordwebhooks.data.Image
import me.binarywriter.discordwebhooks.data.Webhook
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.Bukkit
import org.springframework.stereotype.Component
import java.awt.Color
import java.time.ZonedDateTime
import java.util.*

@Component
class AiModerationPostChatProcessor(private val openAiService: OpenAiService) : PostChatProcessor {
    override suspend fun process(messageContext: MessageContext) {
        if (messageContext.isCancelled) {
            return
        }

        if (!aiModerationConfig.enabled) {
            return
        }

        val plain = messageContext.messageData.plainMessage
        val classification = openAiService.classifyChatMessage(plain)

        if (classification.action == ClassificationAction.NONE) {
            return
        }

        val name = messageContext.messageData.senderUuid.toOfflineCloudPlayer().name()

        postWebhook(messageContext, classification)

        ServerboundTeamMessagePacket(GsonComponentSerializer.gson().serialize(buildText {
            appendBotIcon()
            info("Die Nachricht von ")
            variableValue(name ?: messageContext.messageData.senderUuid.toString())
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
        })).fireAndForget()

        when (classification.action) {
            ClassificationAction.DELETE -> {
                messageContext.messageData.signature?.let { ServerboundMessageDeletePacket(it).fireAndForget() }

                ServerboundHistoryMarkDeletedPacket(
                    messageContext.messageData.messageUuid,
                    "Automod (surf-chat)"
                ).fireAndForget()
            }

            ClassificationAction.MUTE -> {
                val sender = messageContext.messageData.senderUuid.toOfflineCloudPlayer()
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

                sender.punishmentManager.punish(
                    PunishType.MUTE.Expirable(ZonedDateTime.now().plusDays(7))
                        .withNote(note),
                    "Unangemessenes Chat verhalten"
                )
            }

            else -> Unit
        }
    }

    private fun postWebhook(messageContext: MessageContext, classification: ClassificationResult) {
        val senderUuid = messageContext.messageData.senderUuid

        val webhook = Webhook {
            username = "Arty AI Moderation"
            avatarUrl = aiModerationConfig.webhookAvatarUrl
            embed {
                image = Image("https://mc-heads.net/avatar/$senderUuid")

                title = "Chat Nachricht moderiert"
                when (classification.action) {
                    ClassificationAction.SILENT_FLAG -> {
                        description =
                            "Nachricht wurde als unangemessen markiert — bitte überprüfen und ggf. handeln"
                    }

                    ClassificationAction.DELETE -> {
                        description = "Die Chat Nachricht wurde gelöscht."
                    }

                    ClassificationAction.MUTE -> {
                        description =
                            "Die Chat Nachricht wurde gelöscht und der Absender wurde für 7 Tage stumm geschaltet — bitte überprüfen"
                    }

                    else -> Unit
                }

                color = when (classification.action) {
                    ClassificationAction.SILENT_FLAG -> Color.YELLOW
                    ClassificationAction.DELETE -> Color.RED
                    ClassificationAction.MUTE -> Color.MAGENTA
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
                        "[${nameOrUuid(senderUuid)}](${aiModerationConfig.userPanelPrefix}$senderUuid)"
                    inline = true
                }

                val receiverUuid = messageContext.messageData.receiverUuid
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
                    value = messageContext.messageData.server
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