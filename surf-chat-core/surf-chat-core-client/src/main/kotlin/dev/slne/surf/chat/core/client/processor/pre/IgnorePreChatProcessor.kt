package dev.slne.surf.chat.core.client.processor.pre

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.client.processor.ProcessorOrder
import dev.slne.surf.chat.core.common.service.IgnoreService
import java.util.UUID

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

        return IgnoreService.isIgnored(receiver, sender)
    }
}
