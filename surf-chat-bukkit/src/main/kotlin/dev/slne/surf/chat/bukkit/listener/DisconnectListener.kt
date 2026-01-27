package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.bukkit.hook.MiniPlaceholdersHook
import dev.slne.surf.chat.bukkit.message.MessageFormatter
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.userService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class DisconnectListener : Listener {
    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        MessageFormatter.dirty = true

        val user = event.player.user() ?: return

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

        userService.invalidateUser(user.uuid)

        plugin.launch {
            userService.saveUser(user)
        }
    }
}