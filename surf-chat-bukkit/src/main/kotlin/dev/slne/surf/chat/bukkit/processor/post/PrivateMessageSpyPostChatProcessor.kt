package dev.slne.surf.chat.bukkit.processor.post

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.PostChatProcessor
import dev.slne.surf.chat.bukkit.message.MessageFormatter
import dev.slne.surf.chat.core.service.spyService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit

object PrivateMessageSpyPostChatProcessor : PostChatProcessor {
    override suspend fun process(messageContext: MessageContext) {
        if (messageContext.messageData.type != MessageType.DIRECT) {
            return
        }

        val messageFormatter = MessageFormatter()

        spyService.getPrivateMessageSpies(messageContext.messageData.sender.uuid).forEach {
            Bukkit.getPlayer(it)?.sendText {
                append(messageFormatter.formatPmSpy(messageContext.messageData))
            }
        }
    }
}