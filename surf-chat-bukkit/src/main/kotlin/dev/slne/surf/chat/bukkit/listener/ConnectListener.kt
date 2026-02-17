package dev.slne.surf.chat.bukkit.listener

import dev.slne.surf.chat.bukkit.hook.LuckPermsHook
import dev.slne.surf.chat.bukkit.hook.MiniPlaceholdersHook
import dev.slne.surf.chat.bukkit.message.MessageFormatter
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.ignoreService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.minimessage.miniMessage
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
            ignoreService.loadIgnoreList(uuid)
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (plugin.connectionMessageConfig.enabled) {
            event.joinMessage(
                buildText {
                    darkSpacer("[")
                    success("+")
                    darkSpacer("]")
                    append(miniMessage.deserialize(LuckPermsHook.getPrefix(event.player) + event.player.name))
                }
            )
        } else {
            event.joinMessage(null)
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
