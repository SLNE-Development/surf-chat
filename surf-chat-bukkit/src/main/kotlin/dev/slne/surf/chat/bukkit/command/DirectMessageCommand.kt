package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.bukkit.command.argument.userStringArgument
import dev.slne.surf.chat.bukkit.message.MessageFormatter
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.redis.request.DirectMessageRequest
import dev.slne.surf.chat.bukkit.redis.response.DirectMessageResponse
import dev.slne.surf.chat.bukkit.redisApi
import dev.slne.surf.chat.bukkit.util.toUserOrThrow
import dev.slne.surf.chat.core.service.userService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
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
                    appendPrefix()
                    error("Der Spieler wurde nicht gefunden.")
                }
                return@launch
            }

            if (targetUser.uuid == player.uniqueId) {
                player.sendText {
                    appendPrefix()
                    error("Du kannst dir keine Nachrichten senden.")
                }
                return@launch
            }

            val messageData = MessageData(
                Component.text(message),
                messageId,
                player.toUserOrThrow(),
                targetUser,
                sentAt,
                plugin.server,
                null,
                null,
                MessageType.DIRECT
            )

            val messageFormatter = MessageFormatter()
            val isSuccessful =
                redisApi.sendRequest<DirectMessageResponse>(DirectMessageRequest(messageData)).success

            player.sendText {
                when (isSuccessful) {
                    DirectMessageResponse.DirectMessageStatus.SUCCESS -> {
                        append(messageFormatter.formatOutgoingPm(messageData))
                    }

                    DirectMessageResponse.DirectMessageStatus.DIRECT_MESSAGES_DISABLED -> {
                        appendPrefix()
                        error("Der Spieler hat Direktnachrichten deaktiviert.")
                    }

                    else -> {
                        appendPrefix()
                        error("Der Spieler wurde nicht gefunden oder hat Direktnachrichten deaktiviert.")
                    }
                }
            }
        }
    }
}