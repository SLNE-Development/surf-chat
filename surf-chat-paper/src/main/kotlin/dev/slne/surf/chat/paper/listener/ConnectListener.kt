package dev.slne.surf.chat.paper.listener

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.minimessage.miniMessage
import dev.slne.surf.api.paper.util.forEachPlayer
import dev.slne.surf.chat.core.common.service.IgnoreService
import dev.slne.surf.chat.paper.hook.LuckPermsHook
import dev.slne.surf.chat.paper.hook.MiniPlaceholdersHook
import dev.slne.surf.chat.paper.hook.SettingsHook
import dev.slne.surf.chat.paper.message.MessageFormatter
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
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

        if (!plugin.connectionMessageConfig.enabled) {
            return
        }

        val alwaysShow = event.player.hasPermission(PermissionRegistry.CONNECTION_MESSAGE_ALWAYS_SHOW)
        val message = buildJoinMessage(event)

        if (alwaysShow) {
            forEachPlayer {
                it.sendText {
                    append(message)
                }
            }
        } else {
            forEachPlayer {
                if (plugin.checkSettingsHook()) {
                    if (!SettingsHook.hasConnectionMessagesEnabled(it.uniqueId)) {
                        return@forEachPlayer
                    }
                    it.sendText {
                        append(message)
                    }
                } else {
                    it.sendText {
                        append(message)
                    }
                }
            }
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

    private fun buildJoinMessage(event: PlayerJoinEvent): Component = buildText {
        darkSpacer("[")
        success("+")
        darkSpacer("] ")
        append(miniMessage.deserialize(LuckPermsHook.getPrefix(event.player) + event.player.name))
    }
}
