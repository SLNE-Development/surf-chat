package dev.slne.surf.chat.server.config

import dev.slne.surf.chat.server.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import org.spongepowered.configurate.objectmapping.ConfigSerializable

class MessagesConfigHolder {
    @ConfigSerializable
    data class MessageConfig(
        val chatMotds: List<ServerMotdConfig> = listOf(),
        val connectionMessages: List<ServerConnectionMessagesConfig>,

//        val enabled: Boolean = true,
//        val joinMessage: String = "${Colors.DARK_SPACER.miniMessage()}[${Colors.GREEN.miniMessage()}+${Colors.DARK_SPACER.miniMessage()}] <luckperms_prefix> <player_name>",
//        val leaveMessage: String = "${Colors.DARK_SPACER.miniMessage()}[${Colors.RED.miniMessage()}-${Colors.DARK_SPACER.miniMessage()}] <luckperms_prefix> <player_name>",
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
