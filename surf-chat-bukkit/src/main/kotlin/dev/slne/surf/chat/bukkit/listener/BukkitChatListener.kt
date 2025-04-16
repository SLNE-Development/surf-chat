package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.util.history.LoggedMessage
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.historyService
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.UUID

class BukkitChatListener(): Listener {
    @EventHandler
    fun onAsyncChat(event: AsyncChatEvent) {
        val player = event.player
        val message = event.message()
        val messageID = UUID.randomUUID()

        val formattedMessage = plugin.chatFormat.formatMessage(
            message,
            player,
            player,
            ChatMessageType.GLOBAL,
            "N/A",
            messageID,
            true
        )

        Bukkit.getOnlinePlayers().forEach {
            historyService.logCaching(it.uniqueId, LoggedMessage(player.name, "Unknown", formattedMessage), messageID)
        }

        plugin.launch {
            surfChatApi.logMessage(player.uniqueId, ChatMessageType.GLOBAL, message, messageID)
        }

        var formatted = false

        plugin.messageValidator.parse(message, ChatMessageType.GLOBAL, player) {
            event.renderer { _, _, _, viewer ->
                plugin.chatFormat.formatMessage (
                    message,
                    player,
                    if(viewer is Player) viewer else player,
                    ChatMessageType.GLOBAL,
                    "N/A",
                    messageID,
                    false
                )
            }

            formatted = true
        }

        if(!formatted) {
            event.isCancelled = true
        }
    }
}