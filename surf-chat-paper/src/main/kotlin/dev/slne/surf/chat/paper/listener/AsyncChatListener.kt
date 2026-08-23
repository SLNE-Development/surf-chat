package dev.slne.surf.chat.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.messages.adventure.uuidOrNull
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.message.redirector.MessageRedirectorRegistry
import dev.slne.surf.chat.core.client.processor.runPostProcessors
import dev.slne.surf.chat.core.client.processor.runPreProcessors
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.cancel
import dev.slne.surf.core.api.common.SurfCoreApi
import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.OffsetDateTime
import java.util.*

object AsyncChatListener : Listener {
    @EventHandler
    fun onAsyncChat(event: AsyncChatEvent) {
        val sentAt = OffsetDateTime.now()
        val sender = event.player

        val message = event.message()
        val messageId = UUID.randomUUID()

        val signedMessage = event.signedMessage()

        var data = MessageData(
            message,
            messageId,
            sender.uniqueId,
            null,
            sentAt,
            SurfCoreApi.getCurrentServerName(),
            signedMessage.signature(),
            MessageType.GLOBAL
        )

        var cancelled = false

        runBlocking {
            val result = runPreProcessors(MessageContext(data, event.isCancelled, event.viewers()))
            data = result.messageData
            cancelled = result.isCancelled

            if (result.isCancelled) {
                event.cancel()
            }

            event.renderer { _, _, _, viewer ->
                viewer.uuidOrNull()?.let { result.render(it, viewer) } ?: event.message()
            }
        }

        val postContext = MessageContext(data, cancelled, event.viewers())
        val postData = data

        plugin.launch {
            runPostProcessors(postContext)

            MessageRedirectorRegistry.redirectors.forEach {
                it.redirectMessage(signedMessage, postData)
            }
        }
    }
}
