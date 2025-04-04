package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.user.DisplayUser
import net.kyori.adventure.text.Component

interface ChatFormatModel {
    fun formatMessage(rawMessage: Component, sender: DisplayUser, viewer: DisplayUser, messageType: ChatMessageType, channel: String): Component
}