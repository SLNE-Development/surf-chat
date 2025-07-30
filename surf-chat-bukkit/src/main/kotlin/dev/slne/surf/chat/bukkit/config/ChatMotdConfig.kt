package dev.slne.surf.chat.bukkit.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ChatMotdConfig(
    val enabled: Boolean = true,
    val message: String = "<br><dark_gray>--------------------------------------------------<br><br><#f9c353>Willkommen auf dem CastCrafter Community Server!<br><br><#96CFE8>Alle Informationen, inklusive der Serverregeln, findest du auf:<br><#4BB9F9>https://castcrafter.de/server<br><br><dark_gray>--------------------------------------------------\n"
)
