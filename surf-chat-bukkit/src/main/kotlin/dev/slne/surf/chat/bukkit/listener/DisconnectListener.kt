package dev.slne.surf.chat.bukkit.listener

import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class DisconnectListener : Listener {
    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        val user = event.player.user() ?: return
        channelService.getChannel(user)?.let {
            it.leaveAndTransfer(user.channelMember(it) ?: return@let)
        }
    }
}