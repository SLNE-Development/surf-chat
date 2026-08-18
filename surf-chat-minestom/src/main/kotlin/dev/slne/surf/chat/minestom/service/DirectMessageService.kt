package dev.slne.surf.chat.minestom.service

import dev.slne.minestom.lobby.api.chat.RemoteChatSender
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
import dev.slne.surf.chat.core.client.redis.rpc.SendDirectMessageHandledRedisResponse
import dev.slne.surf.chat.core.client.redis.rpc.SendDirectMessageRedisRequest
import dev.slne.surf.chat.core.client.redis.rpc.SignedChatMessage
import dev.slne.surf.chat.core.client.redisApi
import dev.slne.surf.chat.core.client.service.ReplyCache
import dev.slne.surf.chat.minestom.redis.rpc.chatSession
import dev.slne.surf.chat.minestom.redis.rpc.toLobby
import dev.slne.surf.chat.minestom.redis.rpc.toWire
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.redis.request.RequestTimeoutException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.chat.SignedMessage
import java.time.OffsetDateTime
import java.util.*

/**
 * Delivers direct messages between players, no matter which server of the network they are on.
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

        if (result.isCancelled) {
            sender.sendText {
                appendErrorPrefix()
                error("Deine Nachricht konnte nicht zugestellt werden.")
            }
        } else {
            sender.sendSignedMessage(
                message,
                sender.displayName(),
                formatOutgoingPm(messageData)
            )

            val target = ConnectionManager.getOnlineLobbyPlayerByUuid(targetUuid)

            if (target != null) {
                sendPmOnSameServer(sender, target, messageData, message)
            } else {
                sendPmOnDifferentServer(sender, messageData, message)
            }
        }

        runPostProcessors(
            MessageContext(
                messageData,
                result.isCancelled,
                mutableObjectSetOf()
            )
        )

        if (!result.isCancelled) {
            coroutineScope {
                launch { ReplyCache.setLastTarget(sender.uuid, targetUuid) }
                launch { ReplyCache.setLastTarget(targetUuid, sender.uuid) }
            }
        }
    }

    private suspend fun sendPmOnSameServer(
        sender: LobbyPlayer,
        target: LobbyPlayer,
        messageData: MessageData,
        message: SignedMessage,
    ) {
        if (!SettingsHook.hasDirectMessagesEnabled(target.uuid)) return

        target.sendSignedMessage(
            message,
            sender.displayName(),
            formatIncomingPm(messageData)
        )

        if (SettingsHook.hasChatPingsEnabled(target.uuid)) {
            ChatPlatform.playPingSound(target.uuid)
        }
    }

    private suspend fun sendPmOnDifferentServer(
        sender: LobbyPlayer,
        messageData: MessageData,
        message: SignedMessage,
    ) {
        val captured = sender.captureSignedMessage(message, formatIncomingPm(messageData))

        requireNotNull(captured) { "Failed to capture the signed message." }

        try {
            redisApi.sendRequest<SendDirectMessageHandledRedisResponse>(
                SendDirectMessageRedisRequest(messageData, captured.toWire(sender.chatSession())),
            )
        } catch (_: RequestTimeoutException) {
        }
    }

    /**
     * Shows a direct message that was sent on another server to its receiver here.
     *
     * @return whether the receiver is on this server, no matter if they wanted to see the message
     */
    suspend fun handleRemotePm(messageData: MessageData, message: SignedChatMessage): Boolean {
        val target = messageData.receiver
            ?.let { ConnectionManager.getOnlineLobbyPlayerByUuid(it) } ?: return false

        if (!SettingsHook.hasDirectMessagesEnabled(target.uuid)) return true

        val senderUser = messageData.senderUser()

        target.sendRemoteSignedMessage(
            RemoteChatSender(messageData.sender, senderUser.username, message.chatSession()),
            message.toLobby(),
            text(senderUser.username)
        )

        if (SettingsHook.hasChatPingsEnabled(target.uuid)) {
            ChatPlatform.playPingSound(target.uuid)
        }

        return true
    }
}
