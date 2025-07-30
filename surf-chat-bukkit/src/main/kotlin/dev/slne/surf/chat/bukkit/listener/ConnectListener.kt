package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.bukkit.hook.PlaceholderAPIHook
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.pluginmessage.pluginMessageSender
import dev.slne.surf.chat.core.Constants
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ConnectListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.launch(Dispatchers.IO) {
            delay(1000)

            if (plugin.serverName.isEmpty) {
                pluginMessageSender(Constants.CHANNEL_SERVER_REQUEST, event.player) {
                    writeUTF("Requesting data...")
                }
            }
        }

        if (plugin.connectionMessageConfig.config().enabled) {
            event.joinMessage(
                PlaceholderAPIHook.parse(
                    event.player,
                    MiniMessage.miniMessage()
                        .deserialize(plugin.connectionMessageConfig.config().joinMessage)
                )
            )
        }

        if (plugin.chatMotdConfig.config().enabled) {
            event.player.sendText {
                append(
                    MiniMessage.miniMessage().deserialize(
                        PlaceholderAPIHook.parse(
                            event.player,
                            plugin.chatMotdConfig.config().message
                        )
                    )
                )
            }
        }
    }
}
