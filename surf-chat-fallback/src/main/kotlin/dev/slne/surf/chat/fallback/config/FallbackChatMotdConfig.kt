package dev.slne.surf.chat.fallback.config

import dev.slne.surf.chat.core.service.config.ChatMotdConfig
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class FallbackChatMotdConfig (
    override val enabled: Boolean = false,
    override val lines: List<String> = listOf (
        "<dark_gray>*--------------------------------------------------*",
        " ",
        "<white>Willkommen auf dem <#f9c353>CastCrafter Community Server<white>!",
        " ",
        "<#96CFE8>Alle Informationen, inklusive der Serverregeln, findest du auf:",
        "<#4BB9F9>https://castcrafter.de/server",
        " ",
        " ",
        "<dark_gray>*--------------------------------------------------*"
    )
) : ChatMotdConfig