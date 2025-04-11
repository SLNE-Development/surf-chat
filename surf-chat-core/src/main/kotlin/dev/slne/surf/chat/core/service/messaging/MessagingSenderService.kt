package dev.slne.surf.chat.core.service.messaging

import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component

interface MessagingSenderService {
    fun handleSend(server: String, player: String, message: Component, type: ChatMessageType, messageID: String, channel: String,)

    companion object {
        val INSTANCE = requiredService<MessagingSenderService>()
    }
}

val messagingSenderService get() = MessagingSenderService.INSTANCE