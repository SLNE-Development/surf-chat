package dev.slne.surf.chat.bukkit.config

import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.surfapi.core.api.config.createSpongeYmlConfig
import dev.slne.surf.surfapi.core.api.config.manager.SpongeConfigManager
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
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