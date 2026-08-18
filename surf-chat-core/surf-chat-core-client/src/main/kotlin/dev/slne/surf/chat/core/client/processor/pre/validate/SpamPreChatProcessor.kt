package dev.slne.surf.chat.core.client.processor.pre.validate

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.client.config.chatConfig
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.processor.ProcessorOrder
import dev.slne.surf.chat.core.client.util.hasPermission
import dev.slne.surf.chat.core.client.util.sendText
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

fun isRepeatOf(last: String?, current: String) = last != null && last.equals(current, ignoreCase = true)

object SpamPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.VALIDATE
    private val messageRateLimit = Caffeine.newBuilder()
        .expireAfter(Expiry.creating { _, _ -> Duration.ofMillis(chatConfig.spamConfig.interval) })
        .build<UUID, Int>()
    private val lastSendMessageContent = ConcurrentHashMap<UUID, String>()

    override fun process(context: MessageContext): MessageContext {
        val messageData = context.messageData
        val sender = messageData.sender
        val message = messageData.plainMessage

        if (sender.hasPermission(ChatPermissions.BYPASS_FILTER)) {
            return context
        }

        if (isRepeatOf(lastSendMessageContent[sender], message)) {
            sender.sendText {
                appendErrorPrefix()
                error("Du darfst nicht zweimal hintereinander die gleiche Nachricht senden.")
            }
            context.cancel()
            return context
        }

        lastSendMessageContent[sender] = message

        val messagesSent = messageRateLimit.asMap().compute(sender) { _, value -> (value ?: 0) + 1 }!!

        if (messagesSent >= chatConfig.spamConfig.amount) {
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
