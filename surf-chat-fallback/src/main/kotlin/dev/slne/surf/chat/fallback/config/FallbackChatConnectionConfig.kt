package dev.slne.surf.chat.fallback.config

import dev.slne.surf.chat.core.service.config.ChatConnectionConfig
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class FallbackChatConnectionConfig (
    override val enabled: Boolean = true,
    override val joinFormat: String = "<yellow>%player_name% <green>grüßt Keviro.",
    override val leaveFormat: String = "<yellow>%player_name% <red>verabschiedet sich von Keviro.",
) : ChatConnectionConfig
