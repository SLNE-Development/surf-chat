package dev.slne.surf.chat.bukkit.api

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.core.service.historyService
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services.Fallback
import java.util.*

@AutoService(SurfChatApi::class)
class BukkitSurfChatApi() : SurfChatApi, Fallback {
    override suspend fun logMessage(
        player: UUID,
        type: MessageType,
        message: Component,
        messageID: UUID
    ) {
        historyService.write(player, type, message, messageID)
    }
}