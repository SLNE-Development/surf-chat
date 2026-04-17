package dev.slne.surf.chat.paper.listener

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.chat.core.common.service.IgnoreService
import dev.slne.surf.chat.paper.hook.LuckPermsHook
import dev.slne.surf.chat.paper.hook.MiniPlaceholdersHook
import dev.slne.surf.chat.paper.message.MessageFormatter
import dev.slne.surf.chat.paper.plugin
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent
import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object ConnectListener : Listener {
    @EventHandler
    fun onAsyncPlayerConnectionConfigure(event: AsyncPlayerConnectionConfigureEvent) {
        MessageFormatter.dirty = true

        runBlocking {
            val uuid = event.connection.profile.id ?: error("Player has no UUID")
            IgnoreService.loadIgnoreList(uuid)
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (plugin.connectionMessageConfig.enabled) {
            event.joinMessage(
                buildText {
                    darkSpacer("[")
                    success("+")
                    darkSpacer("] ")
                    append(miniMessage.deserialize(LuckPermsHook.getPrefix(event.player) + event.player.name))
                }
            )
        } else {
            event.joinMessage(null)
        }

        if (plugin.chatMotdConfig.enabled) {
            event.player.sendText {
                append(
                    if (plugin.checkMiniPlaceholdersHook()) {
                        MiniPlaceholdersHook.parse(
                            event.player,
                            plugin.chatMotdConfig.message
                        )
                    } else {
                        miniMessage.deserialize(plugin.chatMotdConfig.message)
                    }
                )
            }
        }
    }
}
