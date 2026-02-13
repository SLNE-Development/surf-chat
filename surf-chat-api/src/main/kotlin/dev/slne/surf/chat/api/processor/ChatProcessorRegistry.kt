package dev.slne.surf.chat.api.processor

import java.util.concurrent.CopyOnWriteArrayList

val chatProcessorRegistry = ChatProcessorRegistry()

class ChatProcessorRegistry {
    val preChatProcessors = CopyOnWriteArrayList<PreChatProcessor>()
    val postChatProcessors = CopyOnWriteArrayList<PostChatProcessor>()

    fun clearProcessors() {
        preChatProcessors.clear()
        postChatProcessors.clear()
    }

    fun register(preChatProcessor: PreChatProcessor) {
        preChatProcessors.add(preChatProcessor)
        preChatProcessors.sortBy { it.order }
    }

    fun register(postChatProcessor: PostChatProcessor) {
        postChatProcessors.add(postChatProcessor)
    }
}