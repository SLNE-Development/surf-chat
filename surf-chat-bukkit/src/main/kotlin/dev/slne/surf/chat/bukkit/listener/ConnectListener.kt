package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.bukkit.hook.MiniPlaceholdersHook
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.userService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlinx.coroutines.Dispatchers
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ConnectListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.launch(Dispatchers.IO) {
            val user = userService.loadUserByUuid(event.player.uniqueId) ?: User(
                event.player.name,
                event.player.uniqueId
            )

            userService.cacheUser(user)
        }

        if (plugin.connectionMessageConfig.enabled) {
            event.joinMessage(
                MiniPlaceholdersHook.parse(
                    event.player,
                    plugin.connectionMessageConfig.joinMessage
                )
            )
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
