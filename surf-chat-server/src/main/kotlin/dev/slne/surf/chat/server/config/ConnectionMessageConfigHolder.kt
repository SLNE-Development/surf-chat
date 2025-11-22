package dev.slne.surf.chat.server.config

import dev.slne.surf.chat.server.plugin
import dev.slne.surf.chat.server.util.miniMessage
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import dev.slne.surf.surfapi.core.api.messages.Colors
import org.spongepowered.configurate.objectmapping.ConfigSerializable

class ConnectionMessageConfigHolder {
    @ConfigSerializable
    data class ConnectionMessageConfig(
        val enabled: Boolean = true,
        val joinMessage: String = "${Colors.DARK_SPACER.miniMessage()}[${Colors.GREEN.miniMessage()}+${Colors.DARK_SPACER.miniMessage()}] <luckperms_prefix> <player_name>",
        val leaveMessage: String = "${Colors.DARK_SPACER.miniMessage()}[${Colors.RED.miniMessage()}-${Colors.DARK_SPACER.miniMessage()}] <luckperms_prefix> <player_name>",
    )

    private val configManager: SpongeConfigManager<ConnectionMessageConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            ConnectionMessageConfig::class.java,
            plugin.dataFolder,
            "connections.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            ConnectionMessageConfig::class.java
        )
        reload()
    }

    fun reload() = configManager.reloadFromFile()

    val config get() = configManager.config
}

val connectionMessageConfig = ConnectionMessageConfigHolder()
