package dev.slne.surf.chat.minestom.processor.pre

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.client.processor.ProcessorOrder
import dev.slne.surf.chat.minestom.message.MinestomMessageFormatter

object FormatPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.FORMAT

    override fun process(context: MessageContext): MessageContext {
        val data = context.messageData

        if (data.type == MessageType.GLOBAL) {
            val hasMention = MinestomMessageFormatter.hasMention(data.plainMessage)

            context.render = { viewerUuid, _ ->
                MinestomMessageFormatter.formatGlobal(data, viewerUuid, hasMention)
            }
        }

        return context
    }
}
