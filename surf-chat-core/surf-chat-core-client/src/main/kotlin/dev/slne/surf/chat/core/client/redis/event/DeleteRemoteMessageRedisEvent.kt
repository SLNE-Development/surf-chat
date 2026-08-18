package dev.slne.surf.chat.core.client.redis.event

import dev.slne.surf.chat.api.serializer.SerializableSignature
import dev.slne.surf.redis.event.RedisEvent
import kotlinx.serialization.Serializable

@Serializable
data class DeleteRemoteMessageRedisEvent(
    val messageSignature: SerializableSignature
) : RedisEvent()
