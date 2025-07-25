package dev.slne.surf.chat.bukkit.message

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.api.model.MessageType
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
    override val server: String,
    override val channel: Channel? = null,
    override val signedMessage: SignedMessage? = null,
    override val type: MessageType
) : MessageData
