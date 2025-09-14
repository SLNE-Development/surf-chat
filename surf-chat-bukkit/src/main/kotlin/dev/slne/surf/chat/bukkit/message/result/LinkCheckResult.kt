package dev.slne.surf.chat.bukkit.message.result

import dev.slne.surf.chat.bukkit.plugin
import java.net.URI

data class LinkCheckResult(
    val invalidLink: Boolean,
    val link: String? = null,
) {
    companion object {
        private val urlRegex = Regex(
            "(?:\\w+://)?[\\w.-]+\\.[a-z]{2,}(?:/\\S*)?",
            RegexOption.IGNORE_CASE
        )

        fun of(message: String): LinkCheckResult {
            urlRegex.findAll(message).forEach { match ->
                val rawUrl = match.value
                val url = if ("://" in rawUrl) rawUrl else "http://$rawUrl"

                val domain = runCatching { URI(url).host }
                    .getOrNull()
                    ?.lowercase()
                    ?.removePrefix("www.")
                    ?: return LinkCheckResult(true, rawUrl)

                if (plugin.surfChatConfig.config.allowedDomains.none { domain.endsWith(it.lowercase()) }) {
                    return LinkCheckResult(true, rawUrl)
                }
            }
            return LinkCheckResult(false)
        }
    }
}
