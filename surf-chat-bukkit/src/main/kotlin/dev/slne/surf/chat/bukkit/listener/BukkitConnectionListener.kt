package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.databaseService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class BukkitConnectionListener(): Listener {
    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        plugin.launch {
            databaseService.handleDisconnect(event.player.uniqueId)
        }
    }
}