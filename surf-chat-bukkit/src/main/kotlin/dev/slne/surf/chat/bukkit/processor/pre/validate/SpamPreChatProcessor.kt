package dev.slne.surf.chat.bukkit.processor.pre.validate

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.processor.ProcessorOrder
import dev.slne.surf.chat.bukkit.util.appendWarningPrefix
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.*

object SpamPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.VALIDATE
    private val messageTimestamps = mutableObject2ObjectMapOf<UUID, ObjectList<Long>>()

    override fun process(context: MessageContext): MessageContext {
        val messageData = context.messageData
        val now = System.currentTimeMillis()
        val interval = plugin.surfChatConfig.config.spamConfig.interval
        val timestamps =
            messageTimestamps.getOrPut(messageData.sender.uuid) { mutableObjectListOf<Long>() }
                .apply { removeIf { it < now - interval } }

        timestamps += now

        if (timestamps.size >= plugin.surfChatConfig.config.spamConfig.amount) {
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