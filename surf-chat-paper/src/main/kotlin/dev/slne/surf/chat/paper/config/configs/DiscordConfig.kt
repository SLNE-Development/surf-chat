package dev.slne.surf.chat.paper.config.configs

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class DiscordConfig(
    val enabled: Boolean = false,
    val webhook: String = ""
)