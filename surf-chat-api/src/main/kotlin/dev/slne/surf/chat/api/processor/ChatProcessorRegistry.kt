package dev.slne.surf.chat.api.processor

import java.util.concurrent.CopyOnWriteArrayList

val chatProcessorRegistry = ChatProcessorRegistry()

class ChatProcessorRegistry {
    val preChatProcessors = CopyOnWriteArrayList<PreChatProcessor>()
    val postChatProcessors = CopyOnWriteArrayList<PostChatProcessor>()

    private val registrationLock = Any()

    fun clearProcessors() {
        synchronized(registrationLock) {
            preChatProcessors.clear()
            postChatProcessors.clear()
        }
    }

    fun register(preChatProcessor: PreChatProcessor) {
        synchronized(registrationLock) {
            var index = preChatProcessors.size

            for (existing in preChatProcessors.indices) {
                if (preChatProcessors[existing].order > preChatProcessor.order) {
                    index = existing
                    break
                }
            }

            preChatProcessors.add(index, preChatProcessor)
        }
    }

    fun register(postChatProcessor: PostChatProcessor) {
        synchronized(registrationLock) {
            postChatProcessors.add(postChatProcessor)
        }
    }
}
