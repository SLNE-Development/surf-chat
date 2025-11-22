package dev.slne.surf.chat.paper.listener

import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.channel.channelService
import dev.slne.surf.chat.paper.hook.MiniPlaceholdersHook
import dev.slne.surf.chat.paper.util.channelMember
import dev.slne.surf.chat.paper.util.cloudPlayer
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

        if (SyncValues.connectionMessagesEnabled.get()) {
            event.quitMessage(
                MiniPlaceholdersHook.parse(
                    event.player,
                    SyncValues.connectionMessagesLeave.get()
                )
            )
        }
    }
}