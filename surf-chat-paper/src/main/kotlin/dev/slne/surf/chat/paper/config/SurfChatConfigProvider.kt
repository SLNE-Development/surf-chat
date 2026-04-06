package dev.slne.surf.chat.paper.config

import dev.slne.surf.api.core.config.manager.SpongeConfigManager
import dev.slne.surf.api.core.config.surfConfigApi
import dev.slne.surf.chat.paper.config.configs.SurfChatConfig
import dev.slne.surf.chat.paper.plugin

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