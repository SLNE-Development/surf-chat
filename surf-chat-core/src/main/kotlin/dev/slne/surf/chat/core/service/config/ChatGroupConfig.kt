package dev.slne.surf.chat.core.service.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

interface ChatGroupConfig {
    val name: String
    val servers: List<String>
}
