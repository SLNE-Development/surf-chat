package dev.slne.surf.chat.bukkit.processor.pre

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.bukkit.message.MessageFormatter
import dev.slne.surf.chat.bukkit.processor.ProcessorOrder

object FormatPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.FORMAT

    override fun process(context: MessageContext): MessageContext {
        val data = context.messageData
        val messageFormatter = MessageFormatter

        if (data.type == MessageType.GLOBAL) {
            context.render = { viewerUuid, _ ->
                messageFormatter.formatGlobal(
                    data.withReceiver(viewerUuid)
                )
            }
        }

        return context
    }
}