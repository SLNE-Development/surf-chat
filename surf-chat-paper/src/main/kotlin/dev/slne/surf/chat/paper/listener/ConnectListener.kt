package dev.slne.surf.chat.paper.listener

import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.api.paper.event.common.connection.PlayerJoinMessageEvent
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.api.paper.util.forEachPlayer
import dev.slne.surf.chat.core.client.config.chatConfig
import dev.slne.surf.chat.core.client.hook.LuckPermsHook
import dev.slne.surf.chat.core.client.message.format.buildConnectionMessage
import dev.slne.surf.chat.core.common.service.IgnoreService
import dev.slne.surf.chat.paper.hook.MiniPlaceholdersHook
import dev.slne.surf.chat.core.client.hook.SettingsHook
import dev.slne.surf.chat.paper.message.MessageFormatter
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent
import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
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

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (event.joinMessage() == null) {
            return
        }

        event.joinMessage(null)

        if (!chatConfig.connectionMessageConfig.enabled) {
            return
        }

        val player = event.player
        val alwaysShow = player.hasPermission(PermissionRegistry.CONNECTION_MESSAGE_ALWAYS_SHOW)
        var message = buildConnectionMessage(
            player.name,
            LuckPermsHook.getPrefix(player.uniqueId),
            joined = true
        )

        val messageEvent = PlayerJoinMessageEvent(player, message)

        if (!messageEvent.call()) {
            return
        }

        message = messageEvent.message

        if (alwaysShow) {
            server.broadcast(message)
        } else {
            forEachPlayer { player ->
                if (plugin.checkSettingsHook()) {
                    if (SettingsHook.hasConnectionMessagesEnabled(player.uniqueId)) {
                        player.sendMessage(message)
                    }
                } else {
                    player.sendMessage(message)
                }
            }
        }

        if (chatConfig.chatMotdConfig.enabled) {
            player.sendText {
                append(
                    if (plugin.checkMiniPlaceholdersHook()) {
                        MiniPlaceholdersHook.parse(
                            player,
                            chatConfig.chatMotdConfig.message
                        )
                    } else {
                        miniMessage.deserialize(chatConfig.chatMotdConfig.message)
                    }
                )
            }
        }
    }
}
