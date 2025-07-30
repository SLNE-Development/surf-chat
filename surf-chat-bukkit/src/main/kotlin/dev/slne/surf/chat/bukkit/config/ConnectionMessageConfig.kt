package dev.slne.surf.chat.bukkit.config

import dev.slne.surf.chat.bukkit.util.miniMessage
import dev.slne.surf.surfapi.core.api.messages.Colors
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class ConnectionMessageConfig(
    val enabled: Boolean = true,
    val joinMessage: String = "${Colors.DARK_SPACER.miniMessage()}[${Colors.GREEN.miniMessage()}+${Colors.DARK_SPACER.miniMessage()}] %luckperms_prefix% %player_name%",
    val leaveMessage: String = "${Colors.DARK_SPACER.miniMessage()}[${Colors.RED.miniMessage()}-${Colors.DARK_SPACER.miniMessage()}] %luckperms_prefix% %player_name%",
)
