package dev.slne.surf.chat.api.processor

import dev.slne.surf.chat.api.message.MessageContext

interface PreChatProcessor {
    val order: Int

    fun process(context: MessageContext): MessageContext
}