package dev.slne.surf.chat.bukkit.message

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.server.ChatServer
import dev.slne.surf.chat.core.message.MessageData
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import java.util.*

data class MessageDataImpl(
    override val message: Component,
    override val sender: User,
    override val receiver: User?,
    override val sentAt: Long,
    override val messageUuid: UUID,
    override val server: ChatServer,
    override val channel: Channel? = null,
    override val signedMessage: SignedMessage? = null,
    override val type: MessageType
) : MessageData {
    fun withReceiver(receiver: User?) = copy(receiver = receiver)
    fun withChannel(channel: Channel?) = if (channel != null) {
        copy(channel = channel, type = MessageType.CHANNEL)
    } else {
        this
    }
}
