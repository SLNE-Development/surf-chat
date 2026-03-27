package dev.slne.surf.chat.paper.command.direct

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.chatProcessorRegistry
import dev.slne.surf.chat.core.paper.redisApi
import dev.slne.surf.chat.paper.message.MessageFormatter
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.redis.event.DirectMessageRedisEvent
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfPlayerArgument
import dev.slne.surf.surfapi.bukkit.api.command.executors.playerExecutorSuspend
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.time.OffsetDateTime
import java.util.*

fun directMessageCommand() = commandAPICommand("msg") {
    withPermission(PermissionRegistry.COMMAND_PM)
    withAliases("dm", "w", "whisper", "tell", "pm")
    surfPlayerArgument("target")
    greedyStringArgument("message")

    playerExecutorSuspend { player, args ->
        val target: SurfPlayer by args
        val message: String by args

        if (target.uuid == player.uniqueId) {
            player.sendText {
                appendErrorPrefix()
                error("Du kannst dir selbst keine privaten Nachrichten senden!")
            }
            return@playerExecutorSuspend
        }

        DirectMessageAccess.sendMessage(player, message, target.uuid)
    }
}

object DirectMessageAccess {
    suspend fun sendMessage(sender: Player, message: String, targetUuid: UUID) {
        var messageData = MessageData(
            Component.text(message),
            UUID.randomUUID(),
            sender.uniqueId,
            targetUuid,
            OffsetDateTime.now(),
            SurfCoreApi.getCurrentServerName(),
            null,
            MessageType.DIRECT
        )

        val result = runPreProcessors(MessageContext(messageData, false, mutableObjectSetOf()))
        messageData = result.messageData

        if (!result.isCancelled) {
            sender.sendText {
                append(MessageFormatter.formatOutgoingPm(messageData))
            }
            redisApi.publishEvent(DirectMessageRedisEvent(messageData)).await()
        } else {
            sender.sendText {
                appendErrorPrefix()
                error("Deine Nachricht konnte nicht zugestellt werden.")
            }
        }

        runPostProcessors(
            MessageContext(
                messageData,
                result.isCancelled,
                mutableObjectSetOf()
            )
        )

        ReplyCache.lastMessages[sender.uniqueId] = targetUuid
        ReplyCache.lastMessages[targetUuid] = sender.uniqueId
    }
}

private fun runPreProcessors(
    original: MessageContext
): MessageContext {
    var context = original

    chatProcessorRegistry.preChatProcessors.sortedBy { it.order }.forEach { processor ->
        context = processor.process(context)

        if (context.isCancelled) {
            return context
        }
    }

    return context
}

private suspend fun runPostProcessors(context: MessageContext) =
    chatProcessorRegistry.postChatProcessors.forEach { processor ->
        processor.process(context)
    }