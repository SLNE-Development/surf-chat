package dev.slne.surf.chat.bukkit.processor.pre

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.bukkit.processor.ProcessorOrder
import dev.slne.surf.chat.bukkit.util.isConsole
import dev.slne.surf.chat.bukkit.util.toUserOrNull
import dev.slne.surf.chat.core.service.ignoreService

class CorrectViewersPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.CORRECT_VIEWERS

    override fun process(context: MessageContext): MessageContext {
        context.viewers.removeIf { it.isConsole() }
        context.viewers.removeIf { isIgnored(it.toUserOrNull(), context.messageData.sender) }

        return context
    }

    private fun isIgnored(player: User?, sender: User): Boolean {
        if (player == null) {
            return false
        }

        return ignoreService.getIgnorelist(player.uuid).any { it.target == sender.uuid }
    }
}