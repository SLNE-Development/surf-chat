package dev.slne.surf.chat.core.client.redis.rpc

import dev.slne.surf.redis.request.RedisResponse
import kotlinx.serialization.Serializable

/**
 * Confirms that a [SendSignedMessageRedisRequest] was handled by the server hosting the target.
 */
@Serializable
class SendSignedMessageHandledRedisResponse : RedisResponse()
