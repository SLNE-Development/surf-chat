package dev.slne.surf.chat.core.service.messaging

import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.text.Component
import java.util.UUID

interface MessagingSenderService {
    fun loadServers()

    fun sendData(player: String, target: String, message: Component, type: ChatMessageType, messageID: UUID, channel: String, forwardingServers: ObjectSet<String>)

    companion object {
        val INSTANCE = requiredService<MessagingSenderService>()
    }
}

val messagingSenderService get() = MessagingSenderService.INSTANCE