package dev.slne.surf.chat.core.service.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

interface ChatConfig {
    val connectionConfig: ChatConnectionConfig
    val chatMotdConfig: ChatMotdConfig
    val chatFilterConfig: ChatFilterConfig
    val groups: List<ChatGroupConfig>
}
