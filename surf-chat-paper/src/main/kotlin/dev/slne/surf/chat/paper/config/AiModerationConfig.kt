package dev.slne.surf.chat.paper.config

import dev.slne.surf.api.core.config.createSpongeYmlConfig
import dev.slne.surf.api.core.config.manager.SpongeConfigManager
import dev.slne.surf.api.core.config.surfConfigApi
import dev.slne.surf.chat.paper.plugin
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class AiModerationConfig(
    var enabled: Boolean = true,
    val webhookUrl: String = "",
    val webhookAvatarUrl: String = "https://i.imgur.com/4vEeYfH.png",
    val userPanelPrefix: String = "https://support.castcrafter.de/cloud/cloud-players/",
    val apiKey: String = ""
) {

    companion object {
        private val manager: SpongeConfigManager<AiModerationConfig>

        init {
            surfConfigApi.createSpongeYmlConfig<AiModerationConfig>(
                plugin.dataPath,
                "ai-moderation.yml"
            )
            manager = surfConfigApi.getSpongeConfigManagerForConfig(AiModerationConfig::class.java)
        }

        fun save() {
            manager.save()
        }

        fun reload() {
            manager.reloadFromFile()
        }

        fun getConfig() = manager.config
        fun init() = Unit
    }
}

val aiModerationConfig get() = AiModerationConfig.getConfig()