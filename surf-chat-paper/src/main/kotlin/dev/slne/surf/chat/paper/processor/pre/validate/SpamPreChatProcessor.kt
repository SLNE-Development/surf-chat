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
import java.util.concurrent.ConcurrentHashMap

object SpamPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.VALIDATE
    private val messageRateLimit = Caffeine.newBuilder()
        .expireAfter(Expiry.creating { _, _ -> Duration.ofMillis(plugin.surfChatConfig.config.spamConfig.interval) })
        .build<UUID, Int>()
    private val lastSendMessageContent = ConcurrentHashMap<UUID, String>()

    override fun process(context: MessageContext): MessageContext {
        val messageData = context.messageData
        val sender = messageData.sender
        val message = messageData.plainMessage

        if (sender.hasPermission(PermissionRegistry.BYPASS_FILTER)) {
            return context
        }

        val lastMessage = lastSendMessageContent[sender]

        if (lastMessage != null && lastMessage.equals(message, ignoreCase = true)) {
            sender.sendText {
                appendErrorPrefix()
                error("Du darfst nicht zweimal hintereinander die gleiche Nachricht senden.")
            }
            context.cancel()
            return context
        }

        lastSendMessageContent[sender] = message

        val messagesSent = messageRateLimit.asMap().compute(sender) { _, value -> (value ?: 0) + 1 }!!

        if (messagesSent >= plugin.surfChatConfig.config.spamConfig.amount) {
            sender.sendText {
                appendErrorPrefix()
                error("Du sendest Nachrichten zu schnell. Bitte warte einen Moment.")
            }
            context.cancel()
            return context
        }

        return context
    }
}