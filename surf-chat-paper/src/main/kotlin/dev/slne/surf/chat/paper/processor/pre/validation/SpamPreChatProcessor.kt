package dev.slne.surf.chat.paper.processor.pre.validation

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.processor.ProcessorOrder
import dev.slne.surf.chat.paper.util.appendWarningPrefix
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import org.springframework.stereotype.Component
import java.util.*

@Component
class SpamPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.VALIDATE
    private val messageTimestamps = mutableObject2ObjectMapOf<UUID, ObjectList<Long>>()

    override fun process(context: MessageContext): MessageContext {
        val messageData = context.messageData
        val now = System.currentTimeMillis()
        val interval = SyncValues.spamInterval.get()
        val timestamps =
            messageTimestamps.getOrPut(messageData.sender.uuid) { mutableObjectListOf<Long>() }
                .apply { removeIf { it < now - interval } }

        timestamps += now

        if (timestamps.size >= SyncValues.spamAmount.get()) {
            messageData.sender.sendText {
                appendWarningPrefix()
                error("Du sendest Nachrichten zu schnell. Bitte warte einen Moment.")
            }
            context.cancel()
            return context
        }

        return context
    }
}