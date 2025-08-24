package dev.slne.surf.chat.core.message

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.message.MessageType
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import java.util.*

interface MessageData {
    val message: Component
    val messageUuid: UUID
    val sender: User
    val receiver: User?
    val sentAt: Long
    val server: String
    val channel: Channel?
    val signedMessage: SignedMessage?
    val type: MessageType
}