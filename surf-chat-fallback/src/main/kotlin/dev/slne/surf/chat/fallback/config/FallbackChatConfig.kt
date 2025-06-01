package dev.slne.surf.chat.fallback.config

import dev.slne.surf.chat.core.service.config.ChatConfig
import dev.slne.surf.chat.core.service.config.ChatConnectionConfig
import dev.slne.surf.chat.core.service.config.ChatFilterConfig
import dev.slne.surf.chat.core.service.config.ChatGroupConfig
import dev.slne.surf.chat.core.service.config.ChatMotdConfig
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class FallbackChatConfig (
    override val connectionConfig: ChatConnectionConfig = FallbackChatConnectionConfig(),
    override val chatMotdConfig: ChatMotdConfig = FallbackChatMotdConfig(),
    override val chatFilterConfig: ChatFilterConfig = FallbackChatFilterConfig(),
    override val groups: List<ChatGroupConfig> = listOf(
        FallbackChatGroupConfig(
            "default",
            listOf(
                "server1",
                "server2",
                "server3"
            ),
        )
    )
) : ChatConfig
