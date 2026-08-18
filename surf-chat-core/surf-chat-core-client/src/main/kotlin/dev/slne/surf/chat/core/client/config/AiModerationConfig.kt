package dev.slne.surf.chat.core.client.config

import com.openai.core.LogLevel
import dev.slne.surf.api.core.config.constraints.NotBlank
import dev.slne.surf.api.core.config.constraints.PositiveNumber
import dev.slne.surf.api.core.config.constraints.Range
import dev.slne.surf.api.core.config.createSpongeYmlConfigManager
import dev.slne.surf.api.core.config.manager.SpongeConfigManager
import dev.slne.surf.api.core.config.migration.ConfigMigrationBuilder
import dev.slne.surf.api.core.config.surfConfigApi
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import java.nio.file.Path

@ConfigSerializable
data class AiModerationConfig(
    @Comment("Whether AI-based chat moderation is enabled.")
    var enabled: Boolean = true,

    @Comment("The webhook URL used to send AI moderation alerts.")
    val webhookUrl: String = "",

    @Comment("The log level for the OpenAI client. Set to OFF to disable logging. Available values: OFF, INFO, ERROR, DEBUG.")
    val logLevel: LogLevel = LogLevel.OFF,

    @Comment("The avatar URL used for messages sent to the moderation webhook.")
    val webhookAvatarUrl: String = "https://i.imgur.com/4vEeYfH.png",

    @Comment(
        "The base URL used to link to a moderated player's user panel. " +
                "The player identifier is appended to this URL."
    )
    val userPanelPrefix: String = "https://support.castcrafter.de/players/",

    @Comment("The OpenAI API key used for AI moderation.")
    val apiKey: String = "",

    @Comment("The OpenAI moderation model used to classify chat messages.")
    @NotBlank
    val model: String = "omni-moderation-latest",

    @Comment("Whether players should automatically be muted when a moderation threshold is exceeded.")
    val autoMuteEnabled: Boolean = false,

    @field:Comment("The duration of an automatic mute in days. Must be greater than zero.")
    @PositiveNumber
    val autoMuteDurationDays: Long = 7,

    @Comment(
        "The confidence thresholds for the individual moderation categories. " +
                "Values must be between 0.0 and 1.0. Lower values make moderation more sensitive."
    )
    val thresholds: AiModerationThresholds = AiModerationThresholds()
) {

    companion object {
        private lateinit var manager: SpongeConfigManager<AiModerationConfig>

        fun init(dataPath: Path) {
            manager = surfConfigApi.createSpongeYmlConfigManager<AiModerationConfig>(
                dataPath,
                "ai-moderation.yml",
                ConfigMigrationBuilder()
                    .migration(1) { node ->
                        val userPanelPrefix = node.node("user-panel-prefix")
                        if (!userPanelPrefix.virtual()) {
                            if (userPanelPrefix.string == "https://support.castcrafter.de/core/core-players/") {
                                userPanelPrefix.set("https://support.castcrafter.de/players/")
                            }
                        }
                    }
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
    }
}

@ConfigSerializable
data class AiModerationThresholds(
    @Range(min = 0.0, max = 1.0)
    val harassment: Double = 0.40,

    @Range(min = 0.0, max = 1.0)
    val harassmentThreatening: Double = 0.40,

    @Range(min = 0.0, max = 1.0)
    val hate: Double = 0.40,

    @Range(min = 0.0, max = 1.0)
    val hateThreatening: Double = 0.50,

    @Range(min = 0.0, max = 1.0)
    val illicit: Double = 0.50,

    @Range(min = 0.0, max = 1.0)
    val illicitViolent: Double = 0.60,

    @Range(min = 0.0, max = 1.0)
    val selfHarm: Double = 0.50,

    @Range(min = 0.0, max = 1.0)
    val selfHarmInstructions: Double = 0.50,

    @Range(min = 0.0, max = 1.0)
    val selfHarmIntent: Double = 0.50,

    @Range(min = 0.0, max = 1.0)
    val sexual: Double = 0.50,

    @Range(min = 0.0, max = 1.0)
    val sexualMinors: Double = 0.40,

    @Range(min = 0.0, max = 1.0)
    val violenceGraphic: Double = 0.50
)

val aiModerationConfig get() = AiModerationConfig.getConfig()
