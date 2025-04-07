package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.toDisplayUser
import io.papermc.paper.event.player.AsyncChatEvent
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
            player.toDisplayUser(),
            player.toDisplayUser(),
            ChatMessageType.GLOBAL,
            "N/A",
        )

        plugin.launch {
            surfChatApi.logMessage(player.uniqueId, ChatMessageType.GLOBAL, message)
        }

        event.renderer { _, _, _, _ ->
            formattedMessage
        }
    }
}