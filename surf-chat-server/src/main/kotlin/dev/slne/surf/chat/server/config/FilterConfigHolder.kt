package dev.slne.surf.chat.server.config

import dev.slne.surf.chat.server.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import org.spongepowered.configurate.objectmapping.ConfigSerializable

class FilterConfigHolder {

    @ConfigSerializable
    data class FilterConfig(
        val amount: Int = 5,
        val interval: Long = 3_000,
        val allowedDomains: List<String> = listOf(
            "castcrafter.de",
            "twitch.tv/castcrafter",
            "youtube.com/castcrafter",
            "discord.gg/castcrafter"
        )
    )

    private val configManager: SpongeConfigManager<FilterConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            FilterConfig::class.java,
            plugin.dataFolder,
            "filter.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            FilterConfig::class.java
        )
        reload()
    }

    fun reload() = configManager.reloadFromFile()

    val config get() = configManager.config
}

val filterConfig = FilterConfigHolder()
