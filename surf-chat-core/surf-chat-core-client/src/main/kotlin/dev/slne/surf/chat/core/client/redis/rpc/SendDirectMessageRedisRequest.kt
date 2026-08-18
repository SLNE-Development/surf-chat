package dev.slne.surf.chat.core.client.redis.rpc

import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.redis.request.RedisRequest
import kotlinx.serialization.Serializable

/**
 * Asks the server the receiver of a direct message is on to deliver [message] to them.
 *
 * The request is published to every server; only the one hosting the receiver answers with
 * [SendDirectMessageHandledRedisResponse].
 */
@Serializable
data class SendDirectMessageRedisRequest(
    val messageData: MessageData,
    val message: SignedChatMessage
) : RedisRequest()
