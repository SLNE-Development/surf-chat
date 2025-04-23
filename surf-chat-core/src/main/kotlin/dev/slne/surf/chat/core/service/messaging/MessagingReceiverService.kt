package dev.slne.surf.chat.core.service.messaging

import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component
import java.util.UUID

interface MessagingReceiverService {
    fun handleReceive(server: String, player: String, target: String, message: Component, type: ChatMessageType, messageID: UUID, channel: String,)

    companion object {
        val INSTANCE = requiredService<MessagingReceiverService>()
    }
}

val messagingReceiverService get() = MessagingReceiverService.INSTANCE