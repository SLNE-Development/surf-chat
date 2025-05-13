package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.chatMotdService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.core.service.spyService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class BukkitConnectionListener(): Listener {
    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        plugin.launch {
            channelService.handleDisconnect(event.player)
            databaseService.handleDisconnect(event.player.uniqueId)
            spyService.handleDisconnect(event.player)
        }
    }

    @EventHandler
    fun onConnect(event: PlayerJoinEvent) {
        val player = event.player

        if(chatMotdService.isMotdEnabled()) {
            surfChatApi.sendRawText(player, chatMotdService.getMotd())
        }
    }
}