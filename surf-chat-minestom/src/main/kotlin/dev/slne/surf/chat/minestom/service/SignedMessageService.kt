package dev.slne.surf.chat.minestom.service

import dev.slne.surf.api.core.messages.adventure.text
import dev.slne.surf.api.minestom.chat.RemoteChatSender
import dev.slne.surf.api.minestom.chat.captureSignedMessage
import dev.slne.surf.api.minestom.chat.sendRemoteSignedMessage
import dev.slne.surf.api.minestom.chat.signedChatSession
import dev.slne.surf.api.minestom.extension.ConnectionManager
import dev.slne.surf.chat.core.client.redis.rpc.SendSignedMessageHandledRedisResponse
import dev.slne.surf.chat.core.client.redis.rpc.SendSignedMessageRedisRequest
import dev.slne.surf.chat.core.client.redis.rpc.SignedChatMessage
import dev.slne.surf.chat.core.client.redisApi
import dev.slne.surf.chat.minestom.redis.rpc.chatSession
import dev.slne.surf.chat.minestom.redis.rpc.toRemote
import dev.slne.surf.chat.minestom.redis.rpc.toWire
import dev.slne.surf.redis.request.RequestTimeoutException
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import net.minestom.server.entity.Player
import java.util.*

/**
 * Delivers signed messages to players that are not on this server.
 */
object SignedMessageService {

    /**
     * Asks the server [targetUuid] is on to show [signedMessage] with [component] as its content.
     */
    suspend fun sendRemoteSignedMessage(
        sender: Player,
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
                    captured.toWire(sender.signedChatSession)
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
        val targetPlayer = ConnectionManager.getOnlinePlayerByUuid(target) ?: return false

        targetPlayer.sendRemoteSignedMessage(
            RemoteChatSender(sender, senderName, message.chatSession()),
            message.toRemote(),
            text(senderName)
        )

        return true
    }
}
