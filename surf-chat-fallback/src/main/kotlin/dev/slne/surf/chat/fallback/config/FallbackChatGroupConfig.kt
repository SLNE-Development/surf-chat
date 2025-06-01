package dev.slne.surf.chat.fallback.config

import dev.slne.surf.chat.core.service.config.ChatGroupConfig
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class FallbackChatGroupConfig (
    override val name: String,
    override val servers: List<String> = emptyList()
) : ChatGroupConfig
