package dev.slne.surf.chat.bukkit.message

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.chat.core.message.MessageFormatter

import net.kyori.adventure.text.Component
import kotlin.time.Duration.Companion.minutes

class MessageFormatterImpl(override val message: Component) : MessageFormatter {
    private val pingPattern = Regex("^@(all|a|here|everyone)\\b\\s*", RegexOption.IGNORE_CASE)
    private val hexRegex = Regex("&#[A-Fa-f0-9]{6}")
    private val linkRegex = Regex("(?i)\\b((https?://)?[\\w-]+(\\.[\\w-]+)+(/\\S*)?)\\b")
    private val itemRegex = Regex("\\[(?i)item]")
    private val nameRegexCache = Caffeine.newBuilder()
        .expireAfterWrite(15.minutes)
        .build<String, Regex> {
            Regex("(?<!\\w)@?$it(?!\\w)")
        }

    override fun format(): Boolean {
        TODO("Not yet implemented")
    }
}