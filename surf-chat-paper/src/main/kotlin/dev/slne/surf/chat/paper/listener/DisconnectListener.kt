package dev.slne.surf.chat.paper.listener

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.api.paper.util.forEachPlayer
import dev.slne.surf.chat.core.common.service.IgnoreService
import dev.slne.surf.chat.core.common.service.SpyService
import dev.slne.surf.chat.paper.hook.LuckPermsHook
import dev.slne.surf.chat.paper.hook.SettingsHook
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

object DisconnectListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onDisconnect(event: PlayerQuitEvent) {
        if (event.quitMessage() == null) {
            return
        }

        if (!plugin.connectionMessageConfig.enabled) {
            return
        }

        val alwaysShow = event.player.hasPermission(PermissionRegistry.CONNECTION_MESSAGE_ALWAYS_SHOW)

        if (alwaysShow) {
            forEachPlayer {
                it.sendText {
                    append(buildQuitMessage(event))
                }
            }
        } else {
            forEachPlayer {
                if (plugin.checkSettingsHook() && SettingsHook.hasConnectionMessagesEnabled(event.player.uniqueId)) {
                    it.sendText {
                        append(buildQuitMessage(event))
                    }
                }
            }
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

    private fun buildQuitMessage(event: PlayerQuitEvent): Component = buildText {
        darkSpacer("[")
        error("-")
        darkSpacer("] ")
        append(miniMessage.deserialize(LuckPermsHook.getPrefix(event.player) + event.player.name))
    }
}