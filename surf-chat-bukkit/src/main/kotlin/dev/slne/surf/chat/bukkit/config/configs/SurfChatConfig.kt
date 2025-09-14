package dev.slne.surf.chat.bukkit.config.configs

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class SurfChatConfig(
    val autoDisablingConfig: AutoDisablingConfig = AutoDisablingConfig(),
    val chatServerConfig: ChatServerConfig = ChatServerConfig(),
    val chatMotdConfig: ChatMotdConfig = ChatMotdConfig(),
    val connectionMessageConfig: ConnectionMessageConfig = ConnectionMessageConfig(),
    val spamConfig: SpamConfig = SpamConfig(),

    val allowedDomains: List<String> = mutableListOf(
        "castcrafter.de",
        "twitch.tv/castcrafter",
        "youtube.com/castcrafter",
        "discord.gg/castcrafter"
    )
)
