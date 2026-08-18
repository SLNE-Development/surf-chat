package dev.slne.surf.chat.core.client.config.configs

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
data class ConnectionMessageConfig(
    @field:Comment("Whether join and quit messages are shown in chat at all.")
    val enabled: Boolean = true,
)
