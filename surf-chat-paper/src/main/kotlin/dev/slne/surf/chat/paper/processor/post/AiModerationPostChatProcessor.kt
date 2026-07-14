package dev.slne.surf.chat.paper.processor.post

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationAction
import dev.slne.surf.chat.core.common.aimoderation.ModerationClassificationResult
import dev.slne.surf.chat.core.common.service.DeletionService
import dev.slne.surf.chat.core.paper.PaperChatInstance
import dev.slne.surf.chat.core.paper.redisApi
import dev.slne.surf.chat.paper.ai.OpenAiService
import dev.slne.surf.chat.paper.config.aiModerationConfig
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.redis.ModerationRedisService
import dev.slne.surf.chat.paper.redis.event.TeamMessageRedisEvent
import dev.slne.surf.chat.paper.util.appendBotIcon
import dev.slne.surf.chat.paper.util.hasPermission
import dev.slne.surf.chat.paper.util.webhook.DiscordClient
import dev.slne.surf.chat.paper.util.webhook.DiscordMessages
import dev.slne.surf.punish.api.common.punishment.PunishType
import dev.slne.surf.punish.api.common.user.PunishmentUser
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
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

    private suspend fun postWebhook(
        messageData: MessageData,
        classification: ModerationClassificationResult
    ) = runCatching {
        val senderName = nameOrUuid(messageData.sender)
        val receiverName = messageData.receiver?.let { nameOrUuid(it) }

        val discordClient = DiscordClient(URI.create(aiModerationConfig.webhookUrl).toURL())

        val jsonPayload = DiscordMessages.moderationModerated(
            messageData,
            classification,
            senderName,
            receiverName
        )

        discordClient.use { client ->
            if (!client.sendJson(jsonPayload)) {
                plugin.logger.warning("Discord API rejected the AI moderation webhook request.")
            }
        }
    }.onFailure {
        plugin.logger.warning("Failed to send webhook for AI moderation: ${it.message}")
    }

    private fun nameOrUuid(uuid: UUID): String {
        return Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString()
    }


    suspend fun processMessage(messageData: MessageData) {
        if (messageData.sender.hasPermission(PermissionRegistry.BYPASS_FILTER)) {
            return
        }

        val plain = messageData.plainMessage
        val classification = OpenAiService.classifyChatMessage(plain, messageData.type.value)

        if (classification.action == ModerationClassificationAction.NONE) {
            return
        }

        val name = messageData.senderUser().lastKnownName ?: messageData.sender.toString()

        ModerationRedisService.cache(messageData, classification)
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

                    if (classification.action == ModerationClassificationAction.SILENT_FLAG) {
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
            ModerationClassificationAction.DELETE -> {
                deleteMessage(messageData, classification.action)
            }

            ModerationClassificationAction.MUTE -> {
                deleteMessage(messageData, classification.action)

                if (aiModerationConfig.autoMuteEnabled) {
                    mutePlayer(messageData, classification)
                }
            }

            else -> Unit
        }

        PaperChatInstance.moderationService.logModeration(messageData, classification)
    }

    private suspend fun deleteMessage(
        messageData: MessageData,
        action: ModerationClassificationAction
    ) {
        DeletionService.deleteMessage(
            messageData,
            deletionReason = "AI classification: ${action.name}",
        )
    }

    private suspend fun mutePlayer(
        messageData: MessageData,
        classification: ModerationClassificationResult
    ) {
        val sender = messageData.sender
        val durationDays = aiModerationConfig.autoMuteDurationDays.coerceAtLeast(1)
        val note = buildString {
            append("[AI MODERATION] Schwerwiegendes Chatverhalten: [")
            val scores = classification.flaggedScores.entries
                .sortedByDescending { it.value }

            for (entry in scores) {
                val scorePercent = entry.value * 100
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
