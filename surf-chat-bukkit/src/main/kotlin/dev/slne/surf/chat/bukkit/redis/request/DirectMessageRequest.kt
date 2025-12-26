package dev.slne.surf.chat.bukkit.redis.request

import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.redis.request.RedisRequest
import kotlinx.serialization.Serializable

@Serializable
data class DirectMessageRequest(
    val messageData: MessageData
) : RedisRequest()