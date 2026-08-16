package dev.slne.surf.chat.paper.config.configs

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Setting

@ConfigSerializable
data class SurfChatConfig(
    val itemPlaceholder: Boolean = true,
    val connectionMessageConfig: ConnectionMessageConfig = ConnectionMessageConfig(),
    val spamConfig: SpamConfig = SpamConfig(),
    @Setting("commandTypo")
    val commandTypoConfig: CommandTypoConfig = CommandTypoConfig(),
    @Setting("autoDisabling")
    val autoDisablingConfig: AutoDisablingConfig = AutoDisablingConfig(),
    @Setting("joinMotd")
    val chatMotdConfig: ChatMotdConfig = ChatMotdConfig(),

    val allowedDomains: List<String> = mutableListOf(
        "castcrafter.de",
        "twitch.tv/castcrafter",
        "youtube.com/castcrafter",
        "discord.gg/castcrafter"
    )
)
