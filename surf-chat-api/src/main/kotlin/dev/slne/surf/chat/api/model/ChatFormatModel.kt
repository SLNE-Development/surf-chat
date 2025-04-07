package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.user.DisplayUser
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.*

interface ChatFormatModel {
    fun formatMessage(rawMessage: Component, sender: Player, viewer: Player, messageType: ChatMessageType, channel: String, messageID: UUID): Component
}