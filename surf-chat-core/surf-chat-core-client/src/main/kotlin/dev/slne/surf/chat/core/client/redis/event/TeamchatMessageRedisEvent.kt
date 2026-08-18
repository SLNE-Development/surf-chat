package dev.slne.surf.chat.core.client.redis.event

import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.redis.event.RedisEvent
import kotlinx.serialization.Serializable

@Serializable
data class TeamchatMessageRedisEvent(
    val messageData: MessageData
) : RedisEvent()