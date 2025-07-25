package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.model.MessageType
import dev.slne.surf.chat.bukkit.message.MessageDataImpl
import dev.slne.surf.chat.bukkit.message.MessageFormatterImpl
import dev.slne.surf.chat.bukkit.message.MessageValidatorImpl
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.cancel
import dev.slne.surf.chat.bukkit.util.isConsole
import dev.slne.surf.chat.bukkit.util.player
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

class AsyncChatListener : Listener {
    @EventHandler
    fun onAsyncChat(event: AsyncChatEvent) {
        val player = event.player
        val user = player.user() ?: return

        val message = event.message()
        val messageId = UUID.randomUUID()
        val messageFormatter = MessageFormatterImpl(message)
        val messageValidator = MessageValidatorImpl.componentValidator(message)

        if (!messageValidator.isSuccess(user)) {
            event.cancel()

            player.sendText {
                appendPrefix()
                append(messageValidator.failureMessage)
            }
            return
        }

        val channel = channelService.getChannel(user)
        val time = System.currentTimeMillis()

        if (channel != null) {
            event.viewers().clear()
            event.viewers().addAll(channel.members.mapNotNull { it.player() })
            event.renderer { _, _, _, viewerAudience ->
                messageFormatter.formatChannel(
                    MessageDataImpl(
                        message,
                        user,
                        viewerAudience.user(),
                        time,
                        messageId,
                        "N/A",
                        channel,
                        event.signedMessage(),
                        MessageType.CHANNEL
                    )
                )
            }
        } else {
            event.viewers().removeIf { it.isConsole() } //TODO: Add config option
            event.renderer { _, _, _, viewerAudience ->
                messageFormatter.formatGlobal(
                    MessageDataImpl(
                        message,
                        user,
                        viewerAudience.user(),
                        time,
                        messageId,
                        "N/A",
                        null,
                        event.signedMessage(),
                        MessageType.GLOBAL
                    )
                )
            }
        }

        plugin.launch {
            historyService.logMessage(
                MessageDataImpl(
                    message, user, null, time, messageId, "N/A", channel, event.signedMessage(),
                    if (channel != null) MessageType.CHANNEL else MessageType.GLOBAL
                )
            )
        }
    }
}