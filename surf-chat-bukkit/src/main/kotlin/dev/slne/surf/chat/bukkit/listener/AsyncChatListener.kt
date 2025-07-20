package dev.slne.surf.chat.bukkit.listener

import dev.slne.surf.chat.bukkit.message.MessageFormatterImpl
import dev.slne.surf.chat.bukkit.message.MessageValidatorImpl
import dev.slne.surf.chat.bukkit.util.cancel
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AsyncChatListener : Listener {
    @EventHandler
    fun onAsyncChat(event: AsyncChatEvent) {
        val player = event.player
        val user = player.user() ?: return

        val message = event.message()
        val messageFormatter = MessageFormatterImpl(message)
        val messageValidator = MessageValidatorImpl.componentValidator(message)


        if (!messageValidator.validate(user)) {
            event.cancel()

            player.sendText {
                messageValidator.failureMessage
            }
            return
        }

        val formattedMessage = messageFormatter.format()

        event.renderer { _, displayName, _, viewerAudience ->
            message
        }
    }
}