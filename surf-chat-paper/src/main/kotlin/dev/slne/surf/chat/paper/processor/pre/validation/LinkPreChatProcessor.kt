package dev.slne.surf.chat.paper.processor.pre.validation

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.paper.processor.ProcessorOrder
import dev.slne.surf.chat.paper.util.appendWarningPrefix
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.springframework.stereotype.Component
import java.net.URI

@Component
class LinkPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.VALIDATE

    private val urlRegex = Regex(
        "(?:\\w+://)?[\\w.-]+\\.[a-z]{2,}(?:/\\S*)?",
        RegexOption.IGNORE_CASE
    )

    override fun process(context: MessageContext): MessageContext {
        val messageData = context.messageData

        urlRegex.findAll(messageData.plainMessage).forEach { match ->
            val rawUrl = match.value
            val url = if ("://" in rawUrl) rawUrl else "http://$rawUrl"

            val domain = runCatching { URI(url).host }
                .getOrNull()
                ?.lowercase()
                ?.removePrefix("www.")
                ?: return@forEach

            if (SyncValues.allowedDomains.none { domain.endsWith(it.lowercase()) }) {
                messageData.sender.sendText {
                    appendWarningPrefix()
                    error("Dein Nachricht enthält einen unerlaubten Link!")
                }
                context.cancel()

                return context
            }
        }

        return context
    }
}