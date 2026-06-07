package dev.slne.surf.chat.paper.redis.event

import dev.slne.surf.redis.event.RedisEvent
import kotlinx.serialization.Serializable
import net.kyori.adventure.chat.SignedMessage

@Serializable
data class DeleteRemoteMessageRedisEvent(
    val messageSignature: SignedMessage.Signature
) : RedisEvent()
