package dev.slne.surf.chat.paper.processor.pre

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.paper.processor.ProcessorOrder
import dev.slne.surf.chat.paper.service.SlowChatService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit

object SlowChatPreChatProcessor : PreChatProcessor {
    override val order: Int = ProcessorOrder.SLOW_CHAT

    override fun process(context: MessageContext): MessageContext {
        if (context.messageData.type != MessageType.GLOBAL) {
            return context
        }

        val player = Bukkit.getPlayer(context.messageData.sender) ?: return context

        if (!SlowChatService.checkPlayer(player)) {
            player.sendText {
                appendErrorPrefix()
                error("Bitte warte einen Moment bevor du erneut eine Nachricht sendest.")
            }
            context.cancel()
            return context
        }
        
        return context
    }
}