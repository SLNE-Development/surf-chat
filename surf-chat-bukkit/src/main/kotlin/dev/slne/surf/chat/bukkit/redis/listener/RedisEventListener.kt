package dev.slne.surf.chat.bukkit.redis.listener

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.bukkit.hook.SettingsHook
import dev.slne.surf.chat.bukkit.message.MessageFormatter
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.redis.event.DirectMessageRedisEvent
import dev.slne.surf.chat.bukkit.redis.event.TeamMessageRedisEvent
import dev.slne.surf.chat.bukkit.redis.event.TeamchatMessageRedisEvent
import dev.slne.surf.redis.event.OnRedisEvent
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit
import org.bukkit.Sound

object RedisEventListener {
    @OnRedisEvent
    fun onTeamchatMessage(event: TeamchatMessageRedisEvent) {
        val message = MessageFormatter.formatTeamchat(event.messageData)
        Bukkit.broadcast(message, PermissionRegistry.COMMAND_TEAMCHAT)
    }

    @OnRedisEvent
    fun onTeamMessage(event: TeamMessageRedisEvent) {
        val message = event.message
        Bukkit.broadcast(message, PermissionRegistry.PREFIX_TEAM)
    }

    @OnRedisEvent
    fun onDirectMessage(event: DirectMessageRedisEvent) {
        val targetPlayer = Bukkit.getPlayer(event.messageData.receiver ?: return) ?: return
        val formatter = MessageFormatter

        if (!SettingsHook.hasDirectMessagesEnabled(targetPlayer.uniqueId)) {
            return
        }

        if (SettingsHook.hasChatPingsEnabled(targetPlayer.uniqueId)) {
            plugin.launch(plugin.entityDispatcher(targetPlayer)) {
                targetPlayer.playSound(true) {
                    type(Sound.ENTITY_CHICKEN_EGG)
                }
            }
        }

        targetPlayer.sendText {
            append(formatter.formatIncomingPm(event.messageData))
        }
    }
}