package dev.slne.surf.chat.core.client.processor.post

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.core.client.message.format.formatPmSpy
import dev.slne.surf.chat.core.client.util.sendText
import dev.slne.surf.chat.core.common.service.SpyService

object PrivateMessageSpyPostChatProcessor : PostChatProcessor {
    override suspend fun process(messageContext: MessageContext) {
        val messageData = messageContext.messageData

        if (messageData.type != MessageType.DIRECT) {
            return
        }

        val spies = SpyService.getObservingPlayers(messageData.sender)

        if (spies.isEmpty()) {
            return
        }

        val message = formatPmSpy(messageData)

        for (spy in spies) {
            spy.sendText { append(message) }
        }
    }
}
