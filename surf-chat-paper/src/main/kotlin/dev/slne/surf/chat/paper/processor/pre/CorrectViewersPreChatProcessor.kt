package dev.slne.surf.chat.paper.processor.pre

import dev.slne.surf.api.core.messages.adventure.uuidOrNull
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.common.service.IgnoreService
import dev.slne.surf.chat.core.client.processor.ProcessorOrder
import dev.slne.surf.chat.paper.util.isConsole
import java.util.*

object CorrectViewersPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.CORRECT_VIEWERS

    override fun process(context: MessageContext): MessageContext {
        val sender = context.messageData.sender
        context.viewers.removeIf { it.isConsole() || isIgnored(it.uuidOrNull(), sender) }

        return context
    }

    private fun isIgnored(viewer: UUID?, sender: UUID): Boolean {
        return viewer != null && IgnoreService.isIgnored(viewer, sender)
    }
}