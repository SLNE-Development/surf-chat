package dev.slne.surf.chat.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.ChatContextHolder
import dev.slne.surf.chat.api.InternalChatApi
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.channel.channelService
import dev.slne.surf.chat.paper.message.MessageStatisticsService
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.cancel
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.player.toCloudPlayer
import dev.slne.surf.cloud.api.common.server.CloudServer
import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import java.util.*

class AsyncChatListener : Listener {
    @OptIn(InternalChatApi::class)
    private val messageStatisticsService by lazy {
        ChatContextHolder.instance.context.getBean<MessageStatisticsService>()
    }

    @Autowired
    lateinit var preProcessors: List<PreChatProcessor>

    @Autowired
    lateinit var postProcessors: List<PostChatProcessor>

    @EventHandler
    fun onAsyncChat(event: AsyncChatEvent) {
        val time = System.currentTimeMillis()
        val player = event.player.toCloudPlayer() ?: return

        val server = CloudServer.current()
        val message = event.message()
        val messageId = UUID.randomUUID()

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

        plugin.launch {
            runPostProcessors(
                MessageContext(data, event.isCancelled, event.viewers())
            )
        }
    }

    private fun runPreProcessors(
        original: MessageContext
    ): MessageContext {

        var context = original

        preProcessors
            .sortedBy { it.order }
            .forEach { processor ->
                context = processor.process(context)

                if (context.isCancelled) {
                    return context
                }
            }

        return context
    }

    private suspend fun runPostProcessors(context: MessageContext) =
        postProcessors.forEach { processor ->
            processor.process(context)
        }


    fun isIgnored(player: CloudPlayer?, sender: CloudPlayer): Boolean {
        if (player == null) {
            return false
        }

        return SyncValues.ignoreList
            .firstOrNull { it.user == player.uuid }
            ?.entries
            ?.any { it.target == sender.uuid } == true
    }
}