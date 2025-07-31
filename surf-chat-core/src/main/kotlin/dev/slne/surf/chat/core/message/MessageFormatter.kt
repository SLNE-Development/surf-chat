package dev.slne.surf.chat.core.message

import net.kyori.adventure.text.Component

interface MessageFormatter {
    val message: Component
    fun formatGlobal(messageData: MessageData): Component
    fun formatIncomingPm(messageData: MessageData): Component
    fun formatOutgoingPm(messageData: MessageData): Component
    fun formatTeamchat(messageData: MessageData): Component
    fun formatChannel(messageData: MessageData): Component
    fun formatPmSpy(messageData: MessageData): Component
    fun formatChannelSpy(messageData: MessageData): Component
}