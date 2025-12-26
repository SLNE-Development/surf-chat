package dev.slne.surf.chat.bukkit.redis.event

import dev.slne.surf.redis.event.RedisEvent
import dev.slne.surf.surfapi.core.api.serializer.adventure.component.SerializableComponent
import kotlinx.serialization.Serializable

@Serializable
data class TeamMessageRedisEvent(
    val message: SerializableComponent
) : RedisEvent()