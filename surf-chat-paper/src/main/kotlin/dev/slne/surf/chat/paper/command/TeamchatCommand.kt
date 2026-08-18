package dev.slne.surf.chat.paper.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.slne.surf.api.paper.command.executors.playerExecutorSuspend
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.core.common.service.HistoryService
import dev.slne.surf.chat.core.client.redisApi
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.core.client.redis.ModerationRedisService
import dev.slne.surf.chat.core.client.redis.event.TeamchatMessageRedisEvent
import dev.slne.surf.core.api.common.SurfCoreApi
import net.kyori.adventure.text.Component
import java.time.OffsetDateTime
import java.util.*

fun teamchatCommand() = commandAPICommand("teamchat") {
    withAliases("tc")
    greedyStringArgument("message")
    withPermission(PermissionRegistry.COMMAND_TEAMCHAT)

    playerExecutorSuspend { player, args ->
        val message: String by args
        val messageComponent = Component.text(message)
        val messageId = UUID.randomUUID()
        val messageData = MessageData(
            messageComponent,
            messageId,
            player.uniqueId,
            null,
            OffsetDateTime.now(),
            SurfCoreApi.getCurrentServerName(),
            null,
            MessageType.TEAM
        )

        redisApi.publishEvent(
            TeamchatMessageRedisEvent(
                messageData
            )
        ).await()

        HistoryService.logMessage(messageData)
        ModerationRedisService.cache(messageData)
    }
}