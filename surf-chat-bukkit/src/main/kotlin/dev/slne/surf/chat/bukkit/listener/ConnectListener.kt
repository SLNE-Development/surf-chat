package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.bukkit.hook.MiniPlaceholdersHook
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.pluginmessage.pluginMessageSender
import dev.slne.surf.chat.core.Constants
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ConnectListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.launch(Dispatchers.IO) {

            if (!ALREADY_REQUESTED) {
                delay(1000)
                pluginMessageSender(Constants.CHANNEL_SERVER_REQUEST, event.player) {
                    writeUTF("Requesting data...")
                }
                ALREADY_REQUESTED = true
            }
        }

        if (plugin.connectionMessageConfig.enabled) {
            event.joinMessage(
                MiniPlaceholdersHook.parseAudience(
                    event.player,
                    plugin.connectionMessageConfig.joinMessage
                )
            )
        }

        if (plugin.chatMotdConfig.enabled) {
            event.player.sendText {
                append(
                    MiniPlaceholdersHook.parseAudience(
                        event.player,
                        plugin.chatMotdConfig.message
                    )
                )
            }
        }
    }

    companion object {
        var ALREADY_REQUESTED = false
    }
}
