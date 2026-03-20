package dev.slne.surf.chat.paper.processor.post

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.core.common.service.SpyService
import dev.slne.surf.chat.paper.message.MessageFormatter
import dev.slne.surf.chat.paper.util.sendText

object PrivateMessageSpyPostChatProcessor : PostChatProcessor {
    override suspend fun process(messageContext: MessageContext) {
        if (messageContext.messageData.type != MessageType.DIRECT) {
            return
        }

        SpyService.getObservingPlayers(messageContext.messageData.sender).forEach {
            it.sendText {
                append(MessageFormatter.formatPmSpy(messageContext.messageData))
            }
        }
    }
}