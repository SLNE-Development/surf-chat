package dev.slne.surf.chat.bukkit.config.configs

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ChatServerConfig(
    var internalName: String = "unspecified",
    var displayName: String = CONFIG_DISPLAY_DEFAULT
)

const val CONFIG_DISPLAY_DEFAULT = "Unspecified Server"
