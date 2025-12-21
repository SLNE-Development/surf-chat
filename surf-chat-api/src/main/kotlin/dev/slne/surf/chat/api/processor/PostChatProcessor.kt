package dev.slne.surf.chat.api.processor

import dev.slne.surf.chat.api.message.MessageContext

interface PostChatProcessor {
    suspend fun process(messageContext: MessageContext)
}