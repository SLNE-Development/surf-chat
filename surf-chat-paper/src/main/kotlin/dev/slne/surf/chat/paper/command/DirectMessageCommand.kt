package dev.slne.surf.chat.paper.command

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.chatProcessorRegistry
import dev.slne.surf.chat.paper.message.MessageFormatter
import dev.slne.surf.chat.paper.redis.event.DirectMessageRedisEvent
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.common.surfCoreApi
import dev.slne.surf.core.api.paper.command.argument.surfPlayerArgument
import dev.slne.surf.surfapi.bukkit.api.command.executors.playerExecutorSuspend
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import net.kyori.adventure.text.Component
import java.time.OffsetDateTime
import java.util.*

fun directMessageCommand() = commandAPICommand("msg") {
    withAliases("dm", "w", "whisper", "tell", "pm")
    withPermission("surf.chat.command.msg")
    surfPlayerArgument("target")
    greedyStringArgument("message")

    playerExecutorSuspend { player, args ->
        val target: SurfPlayer by args
        val message: String by args
        val sentAt = OffsetDateTime.now()
        val messageId = UUID.randomUUID()

        if (target.uuid == player.uniqueId) {
            throw CommandAPI.failWithString("Du kannst dir keine Nachrichten senden.")
        }

        var messageData = MessageData(
            Component.text(message),
            messageId,
            player.uniqueId,
            target.uuid,
            sentAt,
            surfCoreApi.getCurrentServerName(),
            null,
            MessageType.DIRECT
        )

        val result = runPreProcessors(MessageContext(messageData, false, mutableObjectSetOf()))
        messageData = result.messageData

        player.sendText {
            append(MessageFormatter.formatOutgoingPm(messageData))
        }
        redisApi.publishEvent(DirectMessageRedisEvent(messageData)).await()

        runPostProcessors(
            MessageContext(
                messageData,
                result.isCancelled,
                mutableObjectSetOf()
            )
        )
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