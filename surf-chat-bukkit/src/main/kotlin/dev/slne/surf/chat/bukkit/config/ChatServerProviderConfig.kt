package dev.slne.surf.chat.bukkit.config

import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi

class ChatServerProviderConfig {
    private val configManager: SpongeConfigManager<ChatServerConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            ChatServerConfig::class.java,
            plugin.dataPath,
            "chat-server.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            ChatServerConfig::class.java
        )
        reload()
    }

    fun edit(action: ChatServerConfig.() -> Unit) {
        configManager.config = configManager.config.apply { action() }
        configManager.save()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val config get() = configManager.config
}