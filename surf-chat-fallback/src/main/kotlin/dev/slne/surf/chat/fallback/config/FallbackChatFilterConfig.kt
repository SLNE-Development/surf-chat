package dev.slne.surf.chat.fallback.config

import dev.slne.surf.chat.core.service.config.ChatFilterConfig
import dev.slne.surf.chat.core.service.config.ChatSpamConfig
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class FallbackChatFilterConfig (
    override val whitelistedDomains: List<String> = listOf(
        "castcrafter.de"
    ),
    override val spamConfig: ChatSpamConfig = FallbackChatSpamConfig()
) : ChatFilterConfig
