package dev.slne.surf.chat.bukkit.processor.pre

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.bukkit.processor.ProcessorOrder
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.ignoreService

object IgnorePreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.IGNORE

    override fun process(context: MessageContext): MessageContext {
        val data = context.messageData

        if (isIgnored(data.receiver, data.sender)) {
            data.sender.sendText {
                appendPrefix()
                error("Deine Nachricht konnte nicht zugestellt werden.")
            }
            context.cancel()
            return context
        }

        return context
    }

    private fun isIgnored(player: User?, sender: User): Boolean {
        if (player == null) {
            return false
        }

        return ignoreService.getIgnorelist(player.uuid).any { it.target == sender.uuid }
    }
}