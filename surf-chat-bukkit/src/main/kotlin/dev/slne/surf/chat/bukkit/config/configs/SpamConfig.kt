package dev.slne.surf.chat.bukkit.config.configs

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class SpamConfig(
    val amount: Int = 5,
    val interval: Long = 3_000
)
