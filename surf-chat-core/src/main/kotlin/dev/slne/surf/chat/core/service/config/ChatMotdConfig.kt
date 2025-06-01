package dev.slne.surf.chat.core.service.config

interface ChatMotdConfig {
    val enabled: Boolean
    val lines: List<String>
}