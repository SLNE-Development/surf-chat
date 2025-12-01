package dev.slne.surf.chat.paper.processor.pre

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.processor.ProcessorOrder
import dev.slne.surf.chat.paper.util.isConsole
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.player.toCloudPlayer
import org.springframework.stereotype.Component

@Component
class CorrectViewersPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.CORRECT_VIEWERS

    override fun process(context: MessageContext): MessageContext {
        context.viewers.removeIf { it.isConsole() }
        context.viewers.removeIf { isIgnored(it.toCloudPlayer(), context.messageData.sender) }

        return context
    }

    private fun isIgnored(player: CloudPlayer?, sender: CloudPlayer): Boolean {
        if (player == null) {
            return false
        }

        return SyncValues.ignoreList
            .firstOrNull { it.user == player.uuid }
            ?.entries
            ?.any { it.target == sender.uuid } == true
    }
}