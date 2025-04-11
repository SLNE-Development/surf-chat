package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.core.service.messaging.MessagingReceiverService
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services.Fallback

@AutoService(MessagingReceiverService::class)
class BukkitMessagingReceiverService(): MessagingReceiverService, Fallback {
    override fun handleReceive(
        server: String,
        player: String,
        target: String,
        message: Component,
        type: ChatMessageType,
        messageID: Long,
        channel: String
    ) {
        TODO("Not yet implemented")
    }
}