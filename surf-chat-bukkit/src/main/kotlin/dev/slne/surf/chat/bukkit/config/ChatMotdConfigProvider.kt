package dev.slne.surf.chat.bukkit.config

import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi

class ChatMotdConfigProvider {
    private val configManager: SpongeConfigManager<ChatMotdConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            ChatMotdConfig::class.java,
            plugin.dataPath,
            "chat-motd.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            ChatMotdConfig::class.java
        )
        reload()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    fun config() = configManager.config
}