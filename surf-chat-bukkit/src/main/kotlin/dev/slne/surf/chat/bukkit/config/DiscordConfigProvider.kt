package dev.slne.surf.chat.bukkit.config

import dev.slne.surf.chat.bukkit.config.configs.DiscordConfig
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi

class DiscordConfigProvider {
    private val configManager: SpongeConfigManager<DiscordConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            DiscordConfig::class.java,
            plugin.dataPath,
            "discord.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            DiscordConfig::class.java
        )
        reload()
    }

    fun reload() {
        configManager.reloadFromFile()
    }

    val config get() = configManager.config
}