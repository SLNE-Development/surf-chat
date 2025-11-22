package dev.slne.surf.chat.server.config

import dev.slne.surf.chat.server.plugin
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import org.spongepowered.configurate.objectmapping.ConfigSerializable

class AutoDisablingConfigHolder {
    @ConfigSerializable
    data class AutoDisablingConfig(
        val enabled: Boolean = false,
        val maximumPlayersBeforeDisable: Int = 50
    )

    private val configManager: SpongeConfigManager<AutoDisablingConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            AutoDisablingConfig::class.java,
            plugin.dataFolder,
            "auto_disabling.yml"
        )
        configManager = surfConfigApi.getSpongeConfigManagerForConfig(
            AutoDisablingConfig::class.java
        )
        reload()
    }

    fun reload() = configManager.reloadFromFile()

    val config get() = configManager.config
}

val autoDisablingConfig = AutoDisablingConfigHolder()
