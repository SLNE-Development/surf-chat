package dev.slne.surf.chat.paper.message

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.core.common.message.MessageData
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.server.CloudServer
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.text.Component
import java.util.*

data class MessageDataImpl(
    override val message: Component,
    override val sender: CloudPlayer,
    override val receiver: CloudPlayer?,
    override val sentAt: Long,
    override val messageUuid: UUID,
    override val server: CloudServer,
    override val channel: Channel? = null,
    override val signedMessage: SignedMessage? = null,
    override val type: MessageType
) : MessageData {
    fun withReceiver(receiver: CloudPlayer?) = copy(receiver = receiver)
    fun withChannel(channel: Channel?) = if (channel != null) {
        copy(channel = channel, type = MessageType.CHANNEL)
    } else {
        this
    }
}
