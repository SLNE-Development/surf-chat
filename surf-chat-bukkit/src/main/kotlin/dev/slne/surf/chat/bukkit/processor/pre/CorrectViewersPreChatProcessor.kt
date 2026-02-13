package dev.slne.surf.chat.bukkit.processor.pre

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.bukkit.processor.ProcessorOrder
import dev.slne.surf.chat.bukkit.util.isConsole
import dev.slne.surf.chat.bukkit.util.uuidOrNull
import dev.slne.surf.chat.core.service.ignoreService
import java.util.*

object CorrectViewersPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.CORRECT_VIEWERS

    override fun process(context: MessageContext): MessageContext {
        context.viewers.removeIf { it.isConsole() }
        context.viewers.removeIf { isIgnored(it.uuidOrNull(), context.messageData.sender) }

        return context
    }

    private fun isIgnored(viewer: UUID?, sender: UUID): Boolean {
        if (viewer == null) {
            return false
        }

        return ignoreService.isIgnored(viewer, sender)
    }
}