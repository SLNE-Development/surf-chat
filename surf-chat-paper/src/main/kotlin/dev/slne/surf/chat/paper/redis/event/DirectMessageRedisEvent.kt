package dev.slne.surf.chat.paper.redis.event

import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.redis.event.RedisEvent
import kotlinx.serialization.Serializable

@Serializable
data class DirectMessageRedisEvent(
    val messageData: MessageData
) : RedisEvent()
