package dev.slne.surf.chat.bukkit.config

import dev.slne.surf.chat.bukkit.util.miniMessage
import dev.slne.surf.surfapi.core.api.messages.Colors
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ConnectionMessageConfig(
    val enabled: Boolean = true,
    val joinMessage: String = "${Colors.VARIABLE_VALUE.miniMessage()}%player% ${Colors.INFO.miniMessage()}grüßt Arty.",
    val leaveMessage: String = "${Colors.VARIABLE_VALUE.miniMessage()}%player% ${Colors.INFO.miniMessage()}verabschiedet sich bei Arty.",
)
