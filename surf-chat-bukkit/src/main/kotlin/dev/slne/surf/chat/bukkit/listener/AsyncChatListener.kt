package dev.slne.surf.chat.bukkit.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.chatProcessorRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.cancel
import dev.slne.surf.chat.bukkit.util.toUserOrThrow
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.core.api.common.surfCoreApi
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

class AsyncChatListener : Listener {
    @EventHandler
    fun onAsyncChat(event: AsyncChatEvent) {
        val time = System.currentTimeMillis()
        val player = event.player.toUserOrThrow()

        val message = event.message()
        val messageId = UUID.randomUUID()

        var data = MessageData(
            message,
            messageId,
            player,
            null,
            time,
            surfCoreApi.getCurrentServerName(),
            channelService.getChannel(player)?.channelName,
            event.signedMessage().signature(),
            MessageType.GLOBAL
        )

        val result = runPreProcessors(MessageContext(data, event.isCancelled, event.viewers()))
        data = result.messageData

        if (result.isCancelled) {
            event.cancel()
        }

        event.renderer { _, _, _, viewer ->
            viewer.toUserOrThrow().let {
                result.render.invoke(it)
            }
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