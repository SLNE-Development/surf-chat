package dev.slne.surf.chat.core.message

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.model.Channel
import net.kyori.adventure.text.Component
import java.util.*

interface MessageData {
    val message: Component
    val messageId: UUID
    val sender: User
    val receiver: User?
    val sentAt: Long
    val server: String
    val channel: Channel?
}