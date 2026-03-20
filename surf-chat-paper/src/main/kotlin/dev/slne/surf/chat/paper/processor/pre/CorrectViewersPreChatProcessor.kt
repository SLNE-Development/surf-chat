package dev.slne.surf.chat.paper.processor.pre

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.common.service.ignoreService
import dev.slne.surf.chat.paper.processor.ProcessorOrder
import dev.slne.surf.chat.paper.util.isConsole
import dev.slne.surf.chat.paper.util.uuidOrNull
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