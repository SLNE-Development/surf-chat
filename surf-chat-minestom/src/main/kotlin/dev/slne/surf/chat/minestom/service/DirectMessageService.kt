package dev.slne.surf.chat.minestom.service

import dev.slne.minestom.lobby.api.extension.ConnectionManager
import dev.slne.minestom.lobby.api.player.LobbyPlayer
import dev.slne.minestom.lobby.api.player.getOnlineLobbyPlayerByUuid
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.messages.adventure.text
import dev.slne.surf.api.core.util.mutableObjectSetOf
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.core.client.hook.SettingsHook
import dev.slne.surf.chat.core.client.message.format.formatIncomingPm
import dev.slne.surf.chat.core.client.message.format.formatOutgoingPm
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import dev.slne.surf.chat.core.client.processor.runPostProcessors
import dev.slne.surf.chat.core.client.processor.runPreProcessors
import dev.slne.surf.chat.core.client.service.ReplyCache
import dev.slne.surf.core.api.common.SurfCoreApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.chat.SignedMessage
import java.time.OffsetDateTime
import java.util.*

/**
 * Delivers direct messages between players that are connected to this server.
 */
object DirectMessageService {

    suspend fun sendMessage(sender: LobbyPlayer, message: SignedMessage, targetUuid: UUID) {
        var messageData = MessageData(
            message.unsignedContent() ?: text(message.message()),
            UUID.randomUUID(),
            sender.uuid,
            targetUuid,
            OffsetDateTime.now(),
            SurfCoreApi.getCurrentServerName(),
            null,
            MessageType.DIRECT
        )

        val result = runPreProcessors(MessageContext(messageData, false, mutableObjectSetOf()))
        messageData = result.messageData

        var delivered = false

        if (result.isCancelled) {
            sender.sendText {
                appendErrorPrefix()
                error("Deine Nachricht konnte nicht zugestellt werden.")
            }
        } else {
            val target = ConnectionManager.getOnlineLobbyPlayerByUuid(targetUuid)

            if (target == null) {
                sender.sendText {
                    appendErrorPrefix()
                    error("Der Spieler ist nicht auf diesem Server.")
                }
            } else {
                sender.sendSignedMessage(
                    message,
                    sender.displayName(),
                    formatOutgoingPm(messageData)
                )

                if (SettingsHook.hasDirectMessagesEnabled(target.uuid)) {
                    target.sendSignedMessage(
                        message,
                        sender.displayName(),
                        formatIncomingPm(messageData)
                    )

                    if (SettingsHook.hasChatPingsEnabled(target.uuid)) {
                        ChatPlatform.playPingSound(targetUuid)
                    }
                }

                delivered = true
            }
        }

        runPostProcessors(
            MessageContext(
                messageData,
                result.isCancelled,
                mutableObjectSetOf()
            )
        )

        if (delivered) {
            coroutineScope {
                launch { ReplyCache.setLastTarget(sender.uuid, targetUuid) }
                launch { ReplyCache.setLastTarget(targetUuid, sender.uuid) }
            }
        }
    }
}
