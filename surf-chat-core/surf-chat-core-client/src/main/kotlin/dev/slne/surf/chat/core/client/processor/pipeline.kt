package dev.slne.surf.chat.core.client.processor

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.chatProcessorRegistry

suspend fun runPreProcessors(context: MessageContext): MessageContext {
    var current = context

    for (processor in chatProcessorRegistry.preChatProcessors) {
        current = processor.processAsync(current)

        if (current.isCancelled) {
            return current
        }
    }

    return current
}

suspend fun runPostProcessors(context: MessageContext) {
    for (processor in chatProcessorRegistry.postChatProcessors) {
        processor.process(context)
    }
}
