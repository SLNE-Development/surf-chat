package dev.slne.surf.chat.api.processor

val chatProcessorRegistry = ChatProcessorRegistry()

class ChatProcessorRegistry {
    val preChatProcessors = mutableListOf<PreChatProcessor>()
    val postChatProcessors = mutableListOf<PostChatProcessor>()

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