package dev.slne.surf.chat.bukkit.config

import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi

class ConnectionMessageConfigProvider {
    private val configManager: SpongeConfigManager<ConnectionMessageConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            ConnectionMessageConfig::class.java,
            plugin.dataPath,
            "connection-messages.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            ConnectionMessageConfig::class.java
        )
        reload()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    fun config() = configManager.config
}