package dev.slne.surf.chat.bukkit.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
data class ChatServerConfig(
    @param:Comment("Internal name of this server, used for identifying the server in the network. This name will automatically requested from the proxy when a player connects to the server.")
    var internalName: String = "unspecified",
    var displayName: String = CONFIG_DISPLAY_DEFAULT
)

const val CONFIG_DISPLAY_DEFAULT = "Unspecified Server"
