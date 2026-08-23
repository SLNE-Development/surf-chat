package dev.slne.surf.chat.core.client.processor.pre.validate

import dev.slne.surf.chat.api.message.MessageContext
import dev.slne.surf.chat.api.processor.PreChatProcessor
import dev.slne.surf.chat.core.client.config.chatConfig
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.processor.ProcessorOrder
import dev.slne.surf.chat.core.client.util.hasPermission
import dev.slne.surf.chat.core.client.util.sendText
import java.net.URI

private val urlRegex = Regex(
    "(?:\\w+://)?[\\w.-]+\\.[a-z]{2,}(?:/\\S*)?",
    RegexOption.IGNORE_CASE
)

fun findDisallowedLink(message: String, allowedDomains: List<String>): String? {
    var lowercasedDomains: Array<String>? = null

    for (match in urlRegex.findAll(message)) {
        val rawUrl = match.value
        val url = if ("://" in rawUrl) rawUrl else "http://$rawUrl"

        val domain = runCatching { URI(url).host }
            .getOrNull()
            ?.lowercase()
            ?.removePrefix("www.")
            ?: continue

        val domains = lowercasedDomains
            ?: Array(allowedDomains.size) { allowedDomains[it].lowercase() }
                .also { lowercasedDomains = it }

        if (domains.none { domain.endsWith(it) }) {
            return rawUrl
        }
    }

    return null
}

object LinkPreChatProcessor : PreChatProcessor {
    override val order = ProcessorOrder.VALIDATE

    override fun process(context: MessageContext): MessageContext {
        val messageData = context.messageData

        if (context.messageData.sender.hasPermission(ChatPermissions.BYPASS_FILTER)) {
            return context
        }

        if (findDisallowedLink(messageData.plainMessage, chatConfig.allowedDomains) != null) {
            messageData.sender.sendText {
                appendWarningPrefix()
                error("Dein Nachricht enthält einen unerlaubten Link!")
            }
            context.cancel()
        }

        return context
    }
}
