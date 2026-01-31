package dev.slne.surf.chat.bukkit.listener

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.bukkit.hook.MiniPlaceholdersHook
import dev.slne.surf.chat.bukkit.message.MessageFormatter
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.ignoreService
import dev.slne.surf.chat.core.service.spyService
import kotlinx.coroutines.launch
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class DisconnectListener : Listener {
    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        MessageFormatter.dirty = true

        if (plugin.connectionMessageConfig.enabled) {
            event.quitMessage(
                MiniPlaceholdersHook.parse(
                    event.player,
                    plugin.connectionMessageConfig.leaveMessage
                )
            )
        } else {
            event.quitMessage(null)
        }
    }

    @EventHandler
    fun onPlayerConnectionClose(event: PlayerConnectionCloseEvent) {
        plugin.launch {
            val uuid = event.playerUniqueId
            launch { ignoreService.cleanup(uuid) }
            spyService.cleanup(uuid)
        }
    }
}