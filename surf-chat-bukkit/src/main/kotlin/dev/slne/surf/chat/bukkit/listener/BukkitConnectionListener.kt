package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.*
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class BukkitConnectionListener() : Listener {
    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        if (connectionService.isEnabled()) {
            event.quitMessage(
                MiniMessage.miniMessage().deserialize(
                    connectionService.getLeaveMessage().replace("%player%", event.player.name)
                )
            )
        } else {
            event.quitMessage(null)
        }

        plugin.launch {
            channelService.handleDisconnect(event.player)
            databaseService.handleDisconnect(event.player.uniqueId)
            spyService.handleDisconnect(event.player.uniqueId)
        }
    }

    @EventHandler
    fun onConnect(event: PlayerJoinEvent) {
        val player = event.player

        if (connectionService.isEnabled()) {
            event.joinMessage(
                MiniMessage.miniMessage().deserialize(
                    connectionService.getJoinMessage().replace("%player%", player.name)
                )
            )
        } else {
            event.joinMessage(null)
        }

        if (chatMotdService.isMotdEnabled()) {
            player.sendText { text(chatMotdService.getMotd()) }
        }
    }
}