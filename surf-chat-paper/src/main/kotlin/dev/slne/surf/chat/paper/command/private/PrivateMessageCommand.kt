package dev.slne.surf.chat.paper.command.private

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.ChatProcessorRegistry
import dev.slne.surf.chat.core.common.ChatContextHolderImpl
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.message.MessageFormatterImpl
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.cloud.api.client.paper.command.args.onlineCloudPlayerArgument
import dev.slne.surf.cloud.api.client.server.current
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.server.CloudServer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component
import org.springframework.beans.factory.getBean
import java.util.*

private val chatProcessorRegistry by lazy {
    ChatContextHolderImpl.instance.context.getBean<ChatProcessorRegistry>()
}

fun directMessageCommand() = commandAPICommand("msg") {
    withAliases("dm", "w", "whisper", "tell", "pm")
    withPermission(SurfChatPermissionRegistry.COMMAND_TELL)
    onlineCloudPlayerArgument("target")
    greedyStringArgument("message")

    playerExecutor { player, args ->
        val target: CloudPlayer by args
        val message: String by args
        val sentAt = System.currentTimeMillis()
        val messageId = UUID.randomUUID()
        val data = MessageData(
            Component.text(message),
            messageId,
            player.uniqueId,
            target.uuid,
            sentAt,
            CloudServer.current().name,
            null,
            null,
            MessageType.PRIVATE
        )

        if (player.uniqueId == target.uuid) {
            return@playerExecutor run {
                player.sendText {
                    appendPrefix()
                    error("Du kannst dir selbst keine Nachrichten senden.")
                }
            }
        }

        handlePrivateMessage(data)
    }
}

fun handlePrivateMessage(messageData: MessageData) {
    var data = messageData
    val player = messageData.sender
    val target = messageData.receiver ?: return

    SyncValues.latestPrivateMessages.removeIf { it.user == player.uuid }
    SyncValues.latestPrivateMessages.add(
        SyncValues.LastPrivateMessage(
            player.uuid,
            target.uuid
        )
    )

    SyncValues.latestPrivateMessages.removeIf { it.target == target.uuid }
    SyncValues.latestPrivateMessages.add(
        SyncValues.LastPrivateMessage(
            target.uuid,
            player.uuid
        )
    )

    val formatter = MessageFormatterImpl(data.message)
    val result = runPreProcessors(MessageContext(data, false, mutableSetOf(data.sender)))

    data = result.messageData

    if (result.isCancelled) {
        return
    }

    data.sender.sendText {
        append(formatter.formatOutgoingPm(data))
    }

    data.receiver?.sendText {
        append(formatter.formatIncomingPm(data))
    }

    plugin.launch {
        runPostProcessors(MessageContext(data, false, mutableSetOf(data.sender)))
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