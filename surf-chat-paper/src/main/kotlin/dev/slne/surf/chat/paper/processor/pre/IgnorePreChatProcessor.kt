package dev.slne.surf.chat.paper.processor.pre

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.common.service.ignoreService
import dev.slne.surf.chat.paper.processor.ProcessorOrder
import java.util.*

object IgnorePreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.IGNORE

    override fun process(context: MessageContext): MessageContext {
        val data = context.messageData

        if (isIgnored(data.receiver, data.sender)) {
            context.cancel()
            return context
        }

        return context
    }

    private fun isIgnored(receiver: UUID?, sender: UUID): Boolean {
        if (receiver == null) {
            return false
        }

        return ignoreService.isIgnored(receiver, sender)
    }
}