package dev.slne.surf.chat.bukkit.listener

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

        event.renderer { source, _, _, viewer ->
            plugin.chatFormat.formatMessage(
                event.message(),
                source.toDisplayUser(),
                viewer.toDisplayUser(),
                ChatMessageType.GLOBAL,
                "N/A",
            )
        }
    }
}