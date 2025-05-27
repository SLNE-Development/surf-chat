package dev.slne.surf.chat.fallback.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ChatConnectionConfig (
    val enabled: Boolean = true,
    val joinFormat: String = "<yellow>%player_name% <green>grüßt Keviro.",
    val leaveFormat: String = "<yellow>%player_name% <red>verabschiedet sich von Keviro.",
)
