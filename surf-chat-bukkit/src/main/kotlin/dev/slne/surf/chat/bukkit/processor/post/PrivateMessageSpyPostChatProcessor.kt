package dev.slne.surf.chat.bukkit.processor.post

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.bukkit.message.MessageFormatter
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.spyService

object PrivateMessageSpyPostChatProcessor : PostChatProcessor {
    override suspend fun process(messageContext: MessageContext) {
        if (messageContext.messageData.type != MessageType.DIRECT) {
            return
        }

        spyService.getPrivateMessageSpies(messageContext.messageData.sender).forEach {
            it.sendText {
                append(MessageFormatter.formatPmSpy(messageContext.messageData))
            }
        }
    }
}