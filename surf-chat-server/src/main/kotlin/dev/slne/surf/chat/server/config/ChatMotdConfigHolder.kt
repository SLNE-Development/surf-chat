package dev.slne.surf.chat.server.config

import dev.slne.surf.chat.server.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import org.spongepowered.configurate.objectmapping.ConfigSerializable

class ChatMotdConfigHolder {
    @ConfigSerializable
    data class ChatMotdConfig(
        val enabled: Boolean = true,
        val message: String = "<br><dark_gray>--------------------------------------------------<br><br><#f9c353>Willkommen auf dem CastCrafter Community Server!<br><br><#96CFE8>Alle Informationen, inklusive der Serverregeln, findest du auf:<br><#4BB9F9>https://castcrafter.de/server<br><br><dark_gray>--------------------------------------------------\n"
    )

    private val configManager: SpongeConfigManager<ChatMotdConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            ChatMotdConfig::class.java,
            plugin.dataFolder,
            "motd.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            ChatMotdConfig::class.java
        )
        reload()
    }

    fun reload() = configManager.reloadFromFile()

    val config get() = configManager.config
}

val chatMotdConfig = ChatMotdConfigHolder()
