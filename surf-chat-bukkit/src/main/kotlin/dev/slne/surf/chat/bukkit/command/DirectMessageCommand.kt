package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.chatProcessorRegistry
import dev.slne.surf.chat.bukkit.command.argument.userStringArgument
import dev.slne.surf.chat.bukkit.message.MessageFormatter
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.redis.event.DirectMessageRedisEvent
import dev.slne.surf.chat.bukkit.redisApi
import dev.slne.surf.chat.bukkit.util.toUserOrThrow
import dev.slne.surf.chat.core.service.userService
import dev.slne.surf.core.api.common.surfCoreApi
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import net.kyori.adventure.text.Component
import java.util.*

fun directMessageCommand() = commandAPICommand("msg") {
    withAliases("dm", "w", "whisper", "tell", "pm")
    withPermission("surf.chat.command.msg")
    userStringArgument("target")
    greedyStringArgument("message")

    playerExecutor { player, args ->
        val target: String by args
        val message: String by args
        val sentAt = System.currentTimeMillis()
        val messageId = UUID.randomUUID()

        plugin.launch {
            val targetUser = userService.findOrLoadByName(target) ?: run {
                player.sendText {
                    appendErrorPrefix()
                    error("Der Spieler wurde nicht gefunden.")
                }
                return@launch
            }

            if (targetUser.uuid == player.uniqueId) {
                player.sendText {
                    appendErrorPrefix()
                    error("Du kannst dir keine Nachrichten senden.")
                }
                return@launch
            }

            var messageData = MessageData(
                Component.text(message),
                messageId,
                player.toUserOrThrow(),
                targetUser,
                sentAt,
                surfCoreApi.getCurrentServerName(),
                null,
                MessageType.DIRECT
            )

            val messageFormatter = MessageFormatter()
            val isOnline = surfCoreApi.getPlayer(targetUser.uuid) != null

            val result = runPreProcessors(MessageContext(messageData, false, mutableObjectSetOf()))
            messageData = result.messageData

            if (isOnline) {
                player.sendText {
                    append(messageFormatter.formatOutgoingPm(messageData))
                }
                redisApi.publishEvent(DirectMessageRedisEvent(messageData))
            } else {
                player.sendText {
                    appendErrorPrefix()
                    error("Der Spieler ist nicht online.")
                }
            }

            plugin.launch {
                runPostProcessors(
                    MessageContext(
                        messageData,
                        result.isCancelled,
                        mutableObjectSetOf()
                    )
                )
            }
        }
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