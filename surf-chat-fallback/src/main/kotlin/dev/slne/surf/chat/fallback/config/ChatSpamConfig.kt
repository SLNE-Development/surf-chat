package dev.slne.surf.chat.fallback.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ChatSpamConfig (
    val maxMessages: Int = 5,
    val timeFrame: Long = 1000L,
)