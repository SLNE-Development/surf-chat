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
    val apiKey: String = "",
    val model: String = "omni-moderation-latest",
    val autoMuteEnabled: Boolean = false,
    val autoMuteDurationDays: Long = 7,
    val thresholds: AiModerationThresholds = AiModerationThresholds()
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

@ConfigSerializable
data class AiModerationThresholds(
    val harassment: Double = 0.85,
    val harassmentThreatening: Double = 0.90,
    val hate: Double = 0.90,
    val hateThreatening: Double = 0.97,
    val illicit: Double = 0.85,
    val illicitViolent: Double = 0.90,
    val selfHarm: Double = 0.90,
    val selfHarmInstructions: Double = 0.90,
    val selfHarmIntent: Double = 0.90,
    val sexual: Double = 0.92,
    val sexualMinors: Double = 0.98,
    val violenceGraphic: Double = 0.95
)

val aiModerationConfig get() = AiModerationConfig.getConfig()
