package dev.slne.surf.chat.bukkit.api

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.entity.Player
import java.util.*

@AutoService(SurfChatApi::class)
class BukkitSurfChatApi() : SurfChatApi, Fallback {
    override suspend fun logMessage(
        player: UUID,
        type: ChatMessageType,
        message: Component,
        messageID: UUID
    ) {
        historyService.write(player, type, message, messageID)
    }

    override fun sendText(player: Player, message: Component, messageID: UUID) {
        val finalMessage = buildText {
            appendPrefix()
            append(message)
        }

        this.sendRawText(player, finalMessage, messageID)
    }

    override fun sendRawText(player: Player, message: Component, messageID: UUID) {
        player.sendMessage(message)
    }
}