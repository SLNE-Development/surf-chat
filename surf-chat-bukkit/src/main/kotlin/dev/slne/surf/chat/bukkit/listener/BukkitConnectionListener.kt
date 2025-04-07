package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.bukkit.model.BukkitChatUser
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.databaseService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class BukkitConnectionListener(): Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.launch {
            databaseService.saveOrUpdateUser(BukkitChatUser(event.player.uniqueId, event.player.name))
        }
    }
}