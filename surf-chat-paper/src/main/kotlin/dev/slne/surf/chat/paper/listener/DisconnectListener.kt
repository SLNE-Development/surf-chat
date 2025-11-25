package dev.slne.surf.chat.paper.listener

import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.channel.channelService
import dev.slne.surf.chat.paper.hook.MiniPlaceholdersHook
import dev.slne.surf.chat.paper.util.channelMember
import dev.slne.surf.chat.paper.util.cloudPlayer
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.server.CloudServer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class DisconnectListener : Listener {
    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        val player = event.cloudPlayer

        channelService.getChannel(player)?.let {
            it.leaveAndTransfer(player.channelMember(it) ?: return@let)
        }

        val disconnectMessage =
            SyncValues.disconnectMessages.firstOrNull { it.first.matches(CloudServer.current().name) }?.second

        if (disconnectMessage != null) {
            event.quitMessage(
                MiniPlaceholdersHook.parse(
                    event.player,
                    disconnectMessage
                )
            )
        } else {
            event.quitMessage(null)
        }
    }
}