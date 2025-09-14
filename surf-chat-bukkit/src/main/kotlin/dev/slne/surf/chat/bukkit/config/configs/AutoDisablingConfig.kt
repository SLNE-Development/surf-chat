package dev.slne.surf.chat.bukkit.config.configs

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class AutoDisablingConfig(
    val enabled: Boolean = false,
    val maximumPlayersBeforeDisable: Int = 50
)
