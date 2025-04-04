package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.type.ChatMessageType
import net.kyori.adventure.text.Component

interface ChatFormatModel {
    val globalFormat: String
    val privateFormat: String
    val channelFormat: String
    val teamFormat: String

    fun formatMessage(rawMessage: Component, sender: ChatUserModel, viewer: ChatUserModel, messageType: ChatMessageType)
}