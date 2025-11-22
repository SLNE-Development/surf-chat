package dev.slne.surf.chat.paper.listener

import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.hook.MiniPlaceholdersHook
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ConnectListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (SyncValues.connectionMessagesEnabled.get()) {
            event.joinMessage(
                MiniPlaceholdersHook.parse(
                    event.player,
                    SyncValues.connectionMessagesJoin.get()
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
