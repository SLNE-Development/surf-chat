package dev.slne.surf.chat.paper.processor.pre

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.processor.ProcessorOrder
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.springframework.stereotype.Component

@Component
class IgnorePreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.IGNORE

    override fun process(context: MessageContext): MessageContext {
        val data = context.messageData
        
        if (isIgnored(data.receiver, data.sender)) {
            data.sender.sendText {
                appendPrefix()
                error("Deine Nachricht konnte nicht zugestellt werden.")
            }
            context.cancel()
            return context
        }

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