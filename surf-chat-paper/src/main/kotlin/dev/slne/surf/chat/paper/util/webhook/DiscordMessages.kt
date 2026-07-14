package dev.slne.surf.chat.paper.util.webhook

import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationAction
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationResult
import dev.slne.surf.chat.paper.config.aiModerationConfig
import dev.slne.surf.chat.paper.util.webhook.components.SeparatorSpacing
import dev.slne.surf.chat.paper.util.webhook.components.componentsV2Message

object DiscordMessages {
    private const val MAX_MESSAGE_PREVIEW = 1000

    fun moderationModerated(
        messageData: MessageData,
        classification: ModerationClassificationResult,
        senderName: String,
        receiverName: String?
    ): String {
        val actionText = when (classification.action) {
            ModerationClassificationAction.SILENT_FLAG -> "Markiert (Bitte überprüfen)"
            ModerationClassificationAction.DELETE -> "Gelöscht"
            ModerationClassificationAction.MUTE -> if (aiModerationConfig.autoMuteEnabled) {
                "Gelöscht & ${aiModerationConfig.autoMuteDurationDays} Tage Stummgeschaltet"
            } else {
                "Gelöscht (schwerwiegend)"
            }

            else -> "Information"
        }

        val accentColor = when (classification.action) {
            ModerationClassificationAction.SILENT_FLAG -> 0xFEE75C
            ModerationClassificationAction.DELETE -> 0xED4245
            ModerationClassificationAction.MUTE -> 0x992D22
            else -> 0x5865F2
        }

        val categories = classification.flaggedScores.entries
            .sortedByDescending { it.value }
            .joinToString("\n") { entry ->
                "- **${entry.key.name}** (${"%.2f".format(entry.value * 100)}%)"
            }
            .ifEmpty { "-# Keine Kategorien markiert" }

        val messagePreview = messageData.plainMessage
            .replace("`", "'")
            .let { if (it.length > MAX_MESSAGE_PREVIEW) it.take(MAX_MESSAGE_PREVIEW) + "…" else it }

        return componentsV2Message {
            container(accentColor = accentColor) {
                section {
                    textDisplay("## Arty AI Moderation")
                    textDisplay(buildString {
                        appendLine("**Aktion:** $actionText")
                        append("**Spieler:** $senderName [View](${aiModerationConfig.userPanelPrefix}${messageData.sender})")
                        append("\n-# ${messageData.sender}")
                        if (receiverName != null) {
                            append("\n**Empfänger:** $receiverName [View](${aiModerationConfig.userPanelPrefix}${messageData.receiver})")
                            append("\n-# ${messageData.receiver}")
                        }
                    })
                    thumbnail(
                        "https://mc-heads.net/avatar/${messageData.sender}",
                        description = "Spielerkopf von $senderName"
                    )
                }
                separator(spacing = SeparatorSpacing.LARGE)

                textDisplay("### Nachricht")
                textDisplay("```\n$messagePreview\n```")
                textDisplay(
                    "-# Server: **${messageData.server}** • Typ: **${messageData.type.value}** • <t:${messageData.sentAt.toEpochSecond()}:f>"
                )

                separator()
                textDisplay("### Markierte Kategorien")
                textDisplay(categories)
            }
        }.toString()
    }
}
