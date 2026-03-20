package dev.slne.surf.chat.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.chatProcessorRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.cancel
import dev.slne.surf.chat.paper.util.uuidOrNull
import dev.slne.surf.core.api.common.surfCoreApi
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.OffsetDateTime
import java.util.*

class AsyncChatListener : Listener {
    @EventHandler
    fun onAsyncChat(event: AsyncChatEvent) {
        val sentAt = OffsetDateTime.now()
        val sender = event.player

        val message = event.message()
        val messageId = UUID.randomUUID()

        var data = MessageData(
            message,
            messageId,
            sender.uniqueId,
            null,
            sentAt,
            surfCoreApi.getCurrentServerName(),
            event.signedMessage().signature(),
            MessageType.GLOBAL
        )

        val result = runPreProcessors(MessageContext(data, event.isCancelled, event.viewers()))
        data = result.messageData

        if (result.isCancelled) {
            event.cancel()
        }

        event.renderer { _, _, _, viewer ->
            viewer.uuidOrNull()?.let { result.render(it, viewer) } ?: event.message()
        }

        plugin.launch {
            runPostProcessors(MessageContext(data, event.isCancelled, event.viewers()))
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
}