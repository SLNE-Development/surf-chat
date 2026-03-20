package dev.slne.surf.chat.paper.processor.post

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.core.service.historyService

object LogPostChatProcessor : PostChatProcessor {
    override suspend fun process(messageContext: MessageContext) {
        historyService.logMessage(messageContext.messageData)
    }
}