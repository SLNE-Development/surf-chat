package dev.slne.surf.chat.core.message

import dev.slne.surf.chat.api.entity.User
import net.kyori.adventure.text.Component

interface MessageData {
    val message: Component
    val sender: User
    val receiver: User?
    val sentAt: Long
}