package dev.slne.surf.chat.api.processor

class ChatProcessorRegistry(
    preChatProcessorsProvider: ObjectProvider<PreChatProcessor>,
    postChatProcessorsProvider: ObjectProvider<PostChatProcessor>
) {
    val preChatProcessors = mutableListOf<PreChatProcessor>()
    val postChatProcessors = mutableListOf<PostChatProcessor>()

    init {
        preChatProcessors.addAll(preChatProcessorsProvider.orderedStream().toList())
        postChatProcessors.addAll(postChatProcessorsProvider.orderedStream().toList())
    }

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