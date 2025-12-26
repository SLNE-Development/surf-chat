package dev.slne.surf.chat.bukkit.redis.listener

import dev.slne.surf.chat.bukkit.message.MessageFormatter
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
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
            .filter { it.hasPermission(SurfChatPermissionRegistry.COMMAND_TEAMCHAT) }.forEach {
                it.sendText {
                    append(formatter.formatTeamchat(event.messageData))
                }
            }
    }

    @OnRedisEvent
    fun onTeamMessage(event: TeamMessageRedisEvent) {
        val message = event.message

        Bukkit.getOnlinePlayers()
            .filter { it.hasPermission(SurfChatPermissionRegistry.PREFIX_TEAM) }.forEach {
                it.sendText {
                    append(message)
                }
            }
    }
}