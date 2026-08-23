package dev.slne.surf.chat.minestom.listener

import dev.slne.minestom.lobby.api.event.EventRegistrar
import dev.slne.minestom.lobby.api.extension.ConnectionManager
import dev.slne.minestom.lobby.api.extension.addListener
import dev.slne.minestom.lobby.api.player.LobbyPlayer
import dev.slne.minestom.lobby.api.player.onlineLobbyPlayers
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.chat.core.client.config.chatConfig
import dev.slne.surf.chat.core.client.hook.LuckPermsHook
import dev.slne.surf.chat.core.client.hook.SettingsHook
import dev.slne.surf.chat.core.client.message.format.buildConnectionMessage
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import dev.slne.surf.chat.core.common.service.IgnoreService
import dev.slne.surf.chat.core.common.service.SpyService
import dev.slne.surf.chat.minestom.message.MinestomMessageFormatter
import net.minestom.server.entity.Player
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerSpawnEvent

/**
 * Handles the chat side of players joining and leaving the server.
 */
class ConnectionListener : EventRegistrar {
    override fun register(node: EventNode<Event>) {
        node.addListener<PlayerSpawnEvent> { event ->
            if (event.isFirstSpawn) {
                onJoin(event.player)
            }
        }

        node.addListener<PlayerDisconnectEvent> { event ->
            onQuit(event.player)
        }
    }

    private fun onJoin(player: Player) {
        MinestomMessageFormatter.invalidateMentionCache()

        val uuid = player.uuid
        ChatPlatform.launchAsync { IgnoreService.loadIgnoreList(uuid) }

        if (chatConfig.connectionMessageConfig.enabled) {
            sendConnectionMessage(player, joined = true)
        }

        if (chatConfig.chatMotdConfig.enabled) {
            player.sendText {
                append(miniMessage.deserialize(chatConfig.chatMotdConfig.message))
            }
        }
    }

    private fun onQuit(player: Player) {
        MinestomMessageFormatter.invalidateMentionCache()

        val uuid = player.uuid

        if (chatConfig.connectionMessageConfig.enabled) {
            sendConnectionMessage(player, joined = false)
        }

        ChatPlatform.launchAsync {
            IgnoreService.cleanup(uuid)
            SpyService.cleanup(uuid)
        }
    }

    private fun sendConnectionMessage(player: Player, joined: Boolean) {
        val message = buildConnectionMessage(
            player.username,
            LuckPermsHook.getPrefix(player.uuid),
            joined = joined
        )

        val alwaysShow =
            (player as LobbyPlayer).hasPermission(ChatPermissions.CONNECTION_MESSAGE_ALWAYS_SHOW)

        if (alwaysShow) {
            ChatPlatform.broadcast(message)
        } else {
            ConnectionManager.onlineLobbyPlayers.forEach { viewer ->
                if (SettingsHook.hasConnectionMessagesEnabled(viewer.uuid)) {
                    viewer.sendMessage(message)
                }
            }
        }
    }
}
