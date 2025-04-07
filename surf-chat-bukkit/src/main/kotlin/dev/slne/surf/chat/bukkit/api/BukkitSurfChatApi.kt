package dev.slne.surf.chat.bukkit.api

import com.github.shynixn.mccoroutine.folia.launch
import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.SurfChatApi
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.util.history.LoggedMessage
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.entity.Player
import java.util.*

@AutoService(SurfChatApi::class)
class BukkitSurfChatApi(): SurfChatApi, Fallback {
    override suspend fun logMessage(player: UUID, type: ChatMessageType, message: Component) {
        historyService.write(player, type, message)
    }

    override fun sendText(player: Player, message: Component, messageID: UUID) {
        val finalMessage = buildText {
            appendPrefix()
            append(message)
        }

        this.sendRawText(player, finalMessage, messageID)
    }

    override fun sendRawText(player: Player, message: Component, messageID: UUID) {
        plugin.launch {
            surfChatApi.logMessage(player.uniqueId, ChatMessageType.INTERNAL, message)
        }

        player.sendText {
            append(message)
        }

        historyService.logCaching(player.uniqueId, LoggedMessage("Unknown", player.name, message), messageID)
    }
}