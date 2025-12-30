package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.redis.event.TeamchatMessageRedisEvent
import dev.slne.surf.chat.bukkit.redisApi
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.core.api.common.surfCoreApi
import net.kyori.adventure.text.Component
import java.util.*

fun teamchatCommand() = commandAPICommand("teamchat", plugin) {
    withAliases("tc")
    greedyStringArgument("message")
    withPermission(SurfChatPermissionRegistry.COMMAND_TEAMCHAT)

    playerExecutor { player, args ->
        val message: String by args
        val messageComponent = Component.text(message)
        val messageId = UUID.randomUUID()
        val messageData = MessageData(
            messageComponent,
            messageId,
            player.user() ?: return@playerExecutor,
            null,
            System.currentTimeMillis(),
            surfCoreApi.getCurrentServerName(),
            null,
            null,
            MessageType.TEAM
        )

        redisApi.publishEvent(
            TeamchatMessageRedisEvent(
                messageData
            )
        )

        plugin.launch {
            historyService.logMessage(messageData)
        }
    }
}