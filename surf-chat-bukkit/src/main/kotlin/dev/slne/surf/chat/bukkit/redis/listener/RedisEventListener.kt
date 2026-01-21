package dev.slne.surf.chat.bukkit.redis.listener

import dev.slne.surf.chat.bukkit.hook.SettingsHook
import dev.slne.surf.chat.bukkit.message.MessageFormatter
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.redis.event.DirectMessageRedisEvent
import dev.slne.surf.chat.bukkit.redis.event.TeamMessageRedisEvent
import dev.slne.surf.chat.bukkit.redis.event.TeamchatMessageRedisEvent
import dev.slne.surf.redis.event.OnRedisEvent
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit

object RedisEventListener {
    @OnRedisEvent
    fun onTeamchatMessage(event: TeamchatMessageRedisEvent) {
        val formatter = MessageFormatter()

        Bukkit.getOnlinePlayers()
            .filter { it.hasPermission(PermissionRegistry.COMMAND_TEAMCHAT) }.forEach {
                it.sendText {
                    append(formatter.formatTeamchat(event.messageData))
                }
            }
    }

    @OnRedisEvent
    fun onTeamMessage(event: TeamMessageRedisEvent) {
        val message = event.message

        Bukkit.getOnlinePlayers()
            .filter { it.hasPermission(PermissionRegistry.PREFIX_TEAM) }.forEach {
                it.sendText {
                    append(message)
                }
            }
    }

    @OnRedisEvent
    fun onDirectMessage(event: DirectMessageRedisEvent) {
        val target = Bukkit.getPlayer(event.messageData.receiver?.uuid ?: return) ?: return
        val formatter = MessageFormatter()

        if (!SettingsHook.hasDirectMessagesEnabled(target.uniqueId)) {
            return
        }

        target.sendText {
            append(formatter.formatIncomingPm(event.messageData))
        }
    }
}