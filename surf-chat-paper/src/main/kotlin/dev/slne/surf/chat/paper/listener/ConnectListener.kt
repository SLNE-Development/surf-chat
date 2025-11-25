package dev.slne.surf.chat.paper.listener

import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.hook.MiniPlaceholdersHook
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.server.CloudServer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ConnectListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val connectionMessage =
            SyncValues.connectMessages.firstOrNull { it.serverPattern.matches(CloudServer.current().name) }?.message
        val motd =
            SyncValues.chatMotds.firstOrNull { it.serverPattern.matches(CloudServer.current().name) }?.message

        if (connectionMessage != null) {
            event.joinMessage(
                MiniPlaceholdersHook.parse(
                    event.player,
                    connectionMessage
                )
            )
        } else {
            event.joinMessage(null)
        }

        if (motd != null) {
            event.player.sendText {
                append(MiniPlaceholdersHook.parse(event.player, motd))
            }
        }
    }
}
