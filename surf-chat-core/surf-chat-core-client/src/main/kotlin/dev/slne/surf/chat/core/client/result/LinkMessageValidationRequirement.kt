package dev.slne.surf.chat.core.client.result

import dev.slne.surf.chat.core.common.message.MessageData
import dev.slne.surf.chat.core.common.message.MessageValidationRequirement
import dev.slne.surf.chat.core.common.util.SyncValues
import org.springframework.stereotype.Component
import java.net.URI

@Component
class LinkMessageValidationRequirement : MessageValidationRequirement {
    private val urlRegex = Regex(
        "(?:\\w+://)?[\\w.-]+\\.[a-z]{2,}(?:/\\S*)?",
        RegexOption.IGNORE_CASE
    )

    override val sendTeamWarning = true

    override fun test(messageData: MessageData): String? {
        urlRegex.findAll(messageData.plainMessage).forEach { match ->
            val rawUrl = match.value
            val url = if ("://" in rawUrl) rawUrl else "http://$rawUrl"

            val domain = runCatching { URI(url).host }
                .getOrNull()
                ?.lowercase()
                ?.removePrefix("www.")
                ?: return null

            if (SyncValues.allowedDomains.none { domain.endsWith(it.lowercase()) }) {
                return null
            }
        }

        return "Deine Nachricht enthält einen unerlaubten Link!"
    }
}
