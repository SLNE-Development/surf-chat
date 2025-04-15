package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.type.ChatMessageType
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.*

interface ChatFormatModel {
    fun formatMessage(rawMessage: Component, sender: Player, viewer: Player, messageType: ChatMessageType, channel: String, messageID: UUID, warn: Boolean): Component
}