package dev.slne.surf.chat.core.client.processor

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.chatProcessorRegistry

suspend fun runPreProcessors(context: MessageContext): MessageContext {
    var current = context

    chatProcessorRegistry.preChatProcessors.sortedBy { it.order }.forEach { processor ->
        current = processor.processAsync(current)

        if (current.isCancelled) {
            return current
        }
    }

    return current
}

suspend fun runPostProcessors(context: MessageContext) =
    chatProcessorRegistry.postChatProcessors.forEach { processor ->
        processor.process(context)
    }
