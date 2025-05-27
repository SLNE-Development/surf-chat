package dev.slne.surf.chat.fallback.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ChatFilterConfig (
    val whitelistedDomains: List<String> = listOf(
        "castcrafter.de"
    ),
    val spamConfig: ChatSpamConfig = ChatSpamConfig()
)
