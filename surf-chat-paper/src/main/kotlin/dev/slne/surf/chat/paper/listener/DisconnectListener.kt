package dev.slne.surf.chat.paper.listener

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.chat.core.common.service.IgnoreService
import dev.slne.surf.chat.core.common.service.SpyService
import dev.slne.surf.chat.paper.hook.LuckPermsHook
import dev.slne.surf.chat.paper.message.MessageFormatter
import dev.slne.surf.chat.paper.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object DisconnectListener : Listener {
    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        MessageFormatter.dirty = true

        if (plugin.connectionMessageConfig.enabled) {
            event.quitMessage(
                buildText {
                    darkSpacer("[")
                    error("-")
                    darkSpacer("] ")
                    append(miniMessage.deserialize(LuckPermsHook.getPrefix(event.player) + event.player.name))
                }
            )
        } else {
            event.quitMessage(null)
        }
    }

    @EventHandler
    fun onPlayerConnectionClose(event: PlayerConnectionCloseEvent) {
        plugin.launch {
            val uuid = event.playerUniqueId
            IgnoreService.cleanup(uuid)
            SpyService.cleanup(uuid)
        }
    }
}