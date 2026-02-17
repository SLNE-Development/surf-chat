package dev.slne.surf.chat.bukkit.config.configs

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ConnectionMessageConfig(
    val enabled: Boolean = true
)
