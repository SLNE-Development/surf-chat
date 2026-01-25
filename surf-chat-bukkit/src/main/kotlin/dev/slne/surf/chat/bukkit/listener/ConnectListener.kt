package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.bukkit.hook.MiniPlaceholdersHook
import dev.slne.surf.chat.bukkit.message.MessageFormatter
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.userService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlinx.coroutines.Dispatchers
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object ConnectListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        MessageFormatter.dirty = true

        plugin.launch(Dispatchers.IO) {
            val user = userService.loadUserOrCreateByUuid(event.player.uniqueId, event.player.name)

            userService.cacheUser(user)
        }

        if (plugin.connectionMessageConfig.enabled) {
            event.joinMessage(
                MiniPlaceholdersHook.parse(
                    event.player,
                    plugin.connectionMessageConfig.joinMessage
                )
            )
        } else {
            event.joinMessage(null)
        }

        if (plugin.chatMotdConfig.enabled) {
            event.player.sendText {
                append(
                    MiniPlaceholdersHook.parse(
                        event.player,
                        plugin.chatMotdConfig.message
                    )
                )
            }
        }
    }
}
