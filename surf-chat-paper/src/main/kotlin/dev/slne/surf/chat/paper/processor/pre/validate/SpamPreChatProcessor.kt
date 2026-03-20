package dev.slne.surf.chat.paper.processor.pre.validate

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.processor.ProcessorOrder
import dev.slne.surf.chat.paper.util.hasPermission
import dev.slne.surf.chat.paper.util.sendText
import java.time.Duration
import java.util.*

object SpamPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.VALIDATE
    private val messageRateLimit = Caffeine.newBuilder()
        .expireAfter(Expiry.creating { _, _ -> Duration.ofMillis(plugin.surfChatConfig.config.spamConfig.interval) })
        .build<UUID, Int>()

    override fun process(context: MessageContext): MessageContext {
        val messageData = context.messageData

        if (context.messageData.sender.hasPermission(PermissionRegistry.BYPASS_FILTER)) {
            return context
        }

        val messagesSent = messageRateLimit.asMap().compute(messageData.sender) { _, value -> (value ?: 0) + 1 }!!

        if (messagesSent >= plugin.surfChatConfig.config.spamConfig.amount) {
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