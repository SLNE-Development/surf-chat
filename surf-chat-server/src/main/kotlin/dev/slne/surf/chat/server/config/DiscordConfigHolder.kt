package dev.slne.surf.chat.server.config

import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import org.spongepowered.configurate.objectmapping.ConfigSerializable

class DiscordConfigHolder {
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

    @ConfigSerializable
    data class DiscordConfig(
        val enabled: Boolean = false,
        val webhook: String = ""
    )
}

val discordConfig = DiscordConfigHolder()