package dev.slne.surf.chat.fallback.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ChatConfig (
    val connectionConfig: ChatConnectionConfig = ChatConnectionConfig(),
    val chatMotdConfig: ChatMotdConfig = ChatMotdConfig(),
    val chatFilterConfig: ChatFilterConfig = ChatFilterConfig(),
    val groups: List<ChatGroupConfig> = listOf(
        ChatGroupConfig(
            "default",
            listOf(
                "server1",
                "server2",
                "server3"
            ),
        )
    )
)
