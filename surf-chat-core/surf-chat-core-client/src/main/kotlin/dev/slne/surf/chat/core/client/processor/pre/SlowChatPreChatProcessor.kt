package dev.slne.surf.chat.core.client.processor.pre

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import dev.slne.surf.chat.core.client.processor.ProcessorOrder
import dev.slne.surf.chat.core.client.service.SlowChatService
import dev.slne.surf.chat.core.client.util.sendText

object SlowChatPreChatProcessor : PreChatProcessor {
    override val order: Int = ProcessorOrder.SLOW_CHAT

    override fun process(context: MessageContext): MessageContext {
        if (context.messageData.type != MessageType.GLOBAL) {
            return context
        }

        val sender = context.messageData.sender

        if (!ChatPlatform.isOnline(sender)) {
            return context
        }

        if (!SlowChatService.checkPlayer(sender)) {
            sender.sendText {
                appendErrorPrefix()
                error("Bitte warte einen Moment bevor du erneut eine Nachricht sendest.")
            }
            context.cancel()
            return context
        }

        return context
    }
}
