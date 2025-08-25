package dev.slne.surf.chat.bukkit.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ChatServerConfig(
    var internalName: String,
    val displayName: String
)
