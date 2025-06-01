package dev.slne.surf.chat.core.service.config

import org.spongepowered.configurate.objectmapping.ConfigSerializable

interface ChatConnectionConfig {
    val enabled: Boolean
    val joinFormat: String
    val leaveFormat: String
}
