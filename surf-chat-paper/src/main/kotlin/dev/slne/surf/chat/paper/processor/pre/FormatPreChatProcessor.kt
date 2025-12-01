package dev.slne.surf.chat.paper.processor.pre

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.paper.message.MessageFormatterImpl
import dev.slne.surf.chat.paper.processor.ProcessorOrder
import org.springframework.stereotype.Component

@Component
class FormatPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.FORMAT

    override fun process(context: MessageContext): MessageContext {
        val data = context.messageData
        val messageFormatter = MessageFormatterImpl(data.message)

        context.render = { viewer ->
            messageFormatter.formatGlobal(
                data.withReceiver(viewer)
            )
        }

        return context
    }
}