package dev.slne.surf.chat.core.client.config

import dev.slne.surf.api.core.config.manager.SpongeConfigManager
import dev.slne.surf.api.core.config.surfConfigApi
import dev.slne.surf.chat.core.client.config.configs.SurfChatConfig
import java.nio.file.Path

class SurfChatConfigProvider(dataPath: Path) {
    private val configManager: SpongeConfigManager<SurfChatConfig>

    init {
        surfConfigApi.createSpongeYmlConfig(
            SurfChatConfig::class.java,
            dataPath,
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

lateinit var chatConfigProvider: SurfChatConfigProvider
    private set

val chatConfig get() = chatConfigProvider.config

fun initChatConfig(dataPath: Path) {
    chatConfigProvider = SurfChatConfigProvider(dataPath)
}
