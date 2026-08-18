package dev.slne.surf.chat.core.client.redis.rpc

import dev.slne.surf.redis.request.RedisResponse
import kotlinx.serialization.Serializable

/**
 * Confirms that a [SendDirectMessageRedisRequest] was handled by the server hosting the receiver.
 */
@Serializable
class SendDirectMessageHandledRedisResponse : RedisResponse()
