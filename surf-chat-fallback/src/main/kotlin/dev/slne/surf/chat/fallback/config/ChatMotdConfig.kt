package dev.slne.surf.chat.fallback.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ChatMotdConfig (
    val enabled: Boolean = false,
    val lines: List<String> = listOf(
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
)