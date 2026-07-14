package dev.slne.surf.chat.paper.processor.post

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.core.common.service.HistoryService
import dev.slne.surf.chat.paper.redis.ModerationRedisService

object LogPostChatProcessor : PostChatProcessor {
    override suspend fun process(messageContext: MessageContext) {
        HistoryService.logMessage(messageContext.messageData)
        ModerationRedisService.cache(messageContext.messageData)
    }
}