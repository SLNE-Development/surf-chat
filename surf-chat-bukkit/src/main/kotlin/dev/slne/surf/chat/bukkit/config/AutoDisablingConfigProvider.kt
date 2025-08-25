package dev.slne.surf.chat.bukkit.config

import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi

class AutoDisablingConfigProvider {
    private val configManager: SpongeConfigManager<AutoDisablingConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            AutoDisablingConfig::class.java,
            plugin.dataPath,
            "auto-disabling.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            AutoDisablingConfig::class.java
        )
        reload()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    fun config() = configManager.config
}