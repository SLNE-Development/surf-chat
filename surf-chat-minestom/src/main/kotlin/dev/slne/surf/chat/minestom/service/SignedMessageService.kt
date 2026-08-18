package dev.slne.surf.chat.minestom.service

import dev.slne.minestom.lobby.api.chat.RemoteChatSender
import dev.slne.minestom.lobby.api.extension.ConnectionManager
import dev.slne.minestom.lobby.api.player.LobbyPlayer
import dev.slne.minestom.lobby.api.player.getOnlineLobbyPlayerByUuid
import dev.slne.surf.api.core.messages.adventure.text
import dev.slne.surf.chat.core.client.redis.rpc.SendSignedMessageHandledRedisResponse
import dev.slne.surf.chat.core.client.redis.rpc.SendSignedMessageRedisRequest
import dev.slne.surf.chat.core.client.redis.rpc.SignedChatMessage
import dev.slne.surf.chat.core.client.redisApi
import dev.slne.surf.chat.minestom.redis.rpc.chatSession
import dev.slne.surf.chat.minestom.redis.rpc.toLobby
import dev.slne.surf.chat.minestom.redis.rpc.toWire
import dev.slne.surf.redis.request.RequestTimeoutException
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import java.util.*

/**
 * Delivers signed messages to players that are not on this server.
 */
object SignedMessageService {

    /**
     * Asks the server [targetUuid] is on to show [signedMessage] with [component] as its content.
     */
    suspend fun sendRemoteSignedMessage(
        sender: LobbyPlayer,
        targetUuid: UUID,
        component: Component,
        signedMessage: SignedMessage,
    ) {
        val captured = sender.captureSignedMessage(signedMessage, component)

        requireNotNull(captured) { "Failed to capture the signed message." }

        try {
            redisApi.sendRequest<SendSignedMessageHandledRedisResponse>(
                SendSignedMessageRedisRequest(
                    sender.uuid,
                    sender.username,
                    targetUuid,
                    captured.toWire(sender.chatSession())
                ),
            )
        } catch (_: RequestTimeoutException) {
        }
    }

    /**
     * Shows a signed message that was sent on another server to its target here.
     *
     * @return whether the target is on this server
     */
    fun handleRemoteSignedMessage(
        sender: UUID,
        senderName: String,
        target: UUID,
        message: SignedChatMessage
    ): Boolean {
        val targetPlayer = ConnectionManager.getOnlineLobbyPlayerByUuid(target) ?: return false

        targetPlayer.sendRemoteSignedMessage(
            RemoteChatSender(sender, senderName, message.chatSession()),
            message.toLobby(),
            text(senderName)
        )

        return true
    }
}
