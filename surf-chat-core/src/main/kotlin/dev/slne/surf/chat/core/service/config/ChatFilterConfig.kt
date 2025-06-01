package dev.slne.surf.chat.core.service.config

interface ChatFilterConfig {
    val whitelistedDomains: List<String>
    val spamConfig: ChatSpamConfig
}
