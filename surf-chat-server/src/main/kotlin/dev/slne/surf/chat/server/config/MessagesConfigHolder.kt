package dev.slne.surf.chat.server.config

import dev.slne.surf.chat.server.plugin
import dev.slne.surf.chat.server.util.miniMessage
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import dev.slne.surf.surfapi.core.api.messages.Colors
import org.spongepowered.configurate.objectmapping.ConfigSerializable

class MessagesConfigHolder {
    @ConfigSerializable
    data class MessageConfig(
        val chatMotds: List<ServerMotdConfig> = listOf(
            ServerMotdConfig(
                "lobby",
                "<br><dark_gray>--------------------------------------------------<br><br><#f9c353>Willkommen auf dem CastCrafter Community Server!<br><br><#96CFE8>Alle Informationen, inklusive der Serverregeln, findest du auf:<br><#4BB9F9>https://castcrafter.de/server<br><br><dark_gray>----------------------------------------------"
            )
        ),
        val connectionMessages: List<ServerConnectionMessagesConfig> = listOf(
            ServerConnectionMessagesConfig(
                "survival",
                "${Colors.DARK_SPACER.miniMessage()}[${Colors.GREEN.miniMessage()}+${Colors.DARK_SPACER.miniMessage()}] <luckperms_prefix> <player_name>",
                "${Colors.DARK_SPACER.miniMessage()}[${Colors.RED.miniMessage()}-${Colors.DARK_SPACER.miniMessage()}] <luckperms_prefix> <player_name>"
            ),
            ServerConnectionMessagesConfig(
                "event",
                "${Colors.DARK_SPACER.miniMessage()}[${Colors.GREEN.miniMessage()}+${Colors.DARK_SPACER.miniMessage()}] <luckperms_prefix> <player_name>",
                "${Colors.DARK_SPACER.miniMessage()}[${Colors.RED.miniMessage()}-${Colors.DARK_SPACER.miniMessage()}] <luckperms_prefix> <player_name>"
            )
        ),
    )

    @ConfigSerializable
    data class ServerMotdConfig(
        val server: String,
        val motd: String
    )

    @ConfigSerializable
    data class ServerConnectionMessagesConfig(
        val server: String,
        val joinMessage: String,
        val leaveMessage: String
    )

    private val configManager: SpongeConfigManager<MessageConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            MessageConfig::class.java,
            plugin.dataFolder,
            "messages.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            MessageConfig::class.java
        )
        reload()
    }

    fun reload() = configManager.reloadFromFile()
    val config get() = configManager.config
}

val messageConfig = MessagesConfigHolder()
