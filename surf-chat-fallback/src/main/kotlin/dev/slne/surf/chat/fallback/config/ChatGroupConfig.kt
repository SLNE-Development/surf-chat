package dev.slne.surf.chat.fallback.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ChatGroupConfig(
    val name: String,
    val servers: List<String> = emptyList()
)
