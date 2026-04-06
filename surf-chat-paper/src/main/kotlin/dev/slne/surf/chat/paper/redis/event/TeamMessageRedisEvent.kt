package dev.slne.surf.chat.paper.redis.event

import dev.slne.surf.api.core.serializer.adventure.component.SerializableComponent
import dev.slne.surf.redis.event.RedisEvent
import kotlinx.serialization.Serializable

@Serializable
data class TeamMessageRedisEvent(
    val message: SerializableComponent
) : RedisEvent()