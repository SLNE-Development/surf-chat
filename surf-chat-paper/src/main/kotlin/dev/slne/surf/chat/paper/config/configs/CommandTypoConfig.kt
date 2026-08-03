package dev.slne.surf.chat.paper.config.configs

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment

@ConfigSerializable
data class CommandTypoConfig(
    @field:Comment("Enable or disable the command typo interception feature")
    val enabled: Boolean = true,

    @field:Comment("List of characters that commonly replace '/' on mistyped keyboard layouts")
    val prefixCharacters: List<String> = listOf("7", "(", ")", "&"),

    @field:Comment("Timeout in seconds before a pending message confirmation expires")
    val confirmationTimeoutSeconds: Long = 45
)
