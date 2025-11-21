package dev.slne.surf.chat.paper.listener

import dev.slne.surf.chat.paper.channel.channelService
import dev.slne.surf.chat.paper.hook.MiniPlaceholdersHook
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.cloudPlayer
import dev.slne.surf.chat.paper.util.user
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class DisconnectListener : Listener {
    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        val player = event.cloudPlayer

        channelService.getChannel(player)?.let {
            it.leaveAndTransfer(user.channelMember(it) ?: return@let)
        }

        if (plugin.connectionMessageConfig.enabled) {
            event.quitMessage(
                MiniPlaceholdersHook.parse(
                    event.player,
                    plugin.connectionMessageConfig.leaveMessage
                )
            )
        }
    }
}