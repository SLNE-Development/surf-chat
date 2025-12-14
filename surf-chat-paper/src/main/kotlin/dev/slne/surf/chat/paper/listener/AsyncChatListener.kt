package dev.slne.surf.chat.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.ChatProcessorRegistry
import dev.slne.surf.chat.core.common.ChatContextHolderImpl
import dev.slne.surf.chat.paper.channel.ChannelService
import dev.slne.surf.chat.paper.channel.channelService
import dev.slne.surf.chat.paper.message.MessageStatisticsService
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.cancel
import dev.slne.surf.chat.paper.util.cloudPlayer
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.player.toCloudPlayer
import dev.slne.surf.cloud.api.common.server.CloudServer
import dev.slne.surf.cloud.api.common.util.classloader.JoinClassLoader
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.springframework.beans.factory.getBean
import org.springframework.stereotype.Component
import java.util.*

@Component
class AsyncChatListener : Listener {
    private val messageStatisticsService by lazy {
        ChatContextHolderImpl.instance.context.getBean<MessageStatisticsService>()
    }

    private val chatProcessorRegistry by lazy {
        ChatContextHolderImpl.instance.context.getBean<ChatProcessorRegistry>()
    }

    @EventHandler
    fun onAsyncChat(event: AsyncChatEvent) {
        val time = System.currentTimeMillis()
        val player = event.cloudPlayer

        val server = CloudServer.current()
        val message = event.message()
        val messageId = UUID.randomUUID()

        println("Current classloader: " + this.javaClass.classLoader)
        println("ChannelService classloader: " + ChannelService::class.java.classLoader)
        val classLoader = ChatContextHolderImpl.instance.context.classLoader as JoinClassLoader
        println("Context classloader: $classLoader")
        println("Find class in this classloader: " )

        var data = MessageData(
            message,
            messageId,
            player.uuid,
            null,
            time,
            server.name,
            channelService.getChannel(player)?.channelName,
            event.signedMessage().signature(),
            MessageType.GLOBAL
        )

        messageStatisticsService.recordMessage()

        val result = runPreProcessors(MessageContext(data, event.isCancelled, event.viewers()))
        data = result.messageData

        if (result.isCancelled) {
            event.cancel()
        }

        event.renderer { _, _, _, viewer ->
            viewer.toCloudPlayer()?.let {
                result.render.invoke(it)
            } ?: buildText {
                error("Internal chat formatting error: viewer is not a CloudPlayer")
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