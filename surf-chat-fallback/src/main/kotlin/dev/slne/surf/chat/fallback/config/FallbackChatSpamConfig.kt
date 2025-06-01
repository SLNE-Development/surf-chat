package dev.slne.surf.chat.fallback.config

import dev.slne.surf.chat.core.service.config.ChatSpamConfig
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class FallbackChatSpamConfig (
    override val maxMessages: Int = 5,
    override val timeFrame: Long = 1000L,
) : ChatSpamConfig