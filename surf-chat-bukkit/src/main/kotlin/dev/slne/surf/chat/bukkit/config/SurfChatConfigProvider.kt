package dev.slne.surf.chat.bukkit.config

import dev.slne.surf.chat.bukkit.config.configs.SurfChatConfig
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi

class SurfChatConfigProvider {
    private val configManager: SpongeConfigManager<SurfChatConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            SurfChatConfig::class.java,
            plugin.dataPath,
            "config.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            SurfChatConfig::class.java
        )
        reload()
    }

    fun edit(actions: SurfChatConfig.() -> Unit) {
        configManager.config = configManager.config.apply { actions() }
        configManager.save()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val config get() = configManager.config
}