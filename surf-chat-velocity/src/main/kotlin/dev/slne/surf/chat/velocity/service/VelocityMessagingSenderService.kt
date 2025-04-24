package dev.slne.surf.chat.velocity.service

import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.core.service.messaging.MessagingSenderService
import net.kyori.adventure.text.Component
import java.util.UUID

class VelocityMessagingSenderService(): MessagingSenderService {
    override fun loadServers() {
        TODO("Not implemented on proxy")
    }

    override fun sendData (
        player: String,
        target: String,
        message: Component,
        type: ChatMessageType,
        messageID: UUID,
        channel: String
    ) {
        TODO("Not yet implemented")
    }
}