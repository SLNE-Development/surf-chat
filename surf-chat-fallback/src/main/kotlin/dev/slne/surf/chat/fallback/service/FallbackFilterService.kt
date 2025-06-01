package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.api.type.MessageValidationResult
import dev.slne.surf.chat.core.service.FilterService
import dev.slne.surf.chat.core.service.config.ChatFilterConfig
import dev.slne.surf.chat.core.service.config.ChatSpamConfig
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.chat.fallback.config.FallbackChatFilterConfig
import dev.slne.surf.chat.fallback.config.FallbackChatSpamConfig
import dev.slne.surf.chat.fallback.util.toPlainText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services
import java.net.URI
import java.util.Locale
import java.util.UUID

@AutoService(FilterService::class)
class FallbackFilterService : FilterService, Services.Fallback {
    private val allowedDomains = mutableObjectSetOf<String>()
    private val messageTimestamps = mutableMapOf<UUID, MutableList<Long>>()
    private val validCharactersRegex = "^[\\u0000-\\u007FäöüÄÖÜß€@£¥|²³µ½¼¾«»¡¿°§´`^~¨]+$".toRegex()
    private val urlRegex =
        "((http|https|ftp)://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?".toRegex(RegexOption.IGNORE_CASE)

    private var messageLimitSeconds = 1000L
    private var messageLimitCount = 10

    override fun find(message: Component, user: ChatUser): MessageValidationResult {
        if (denylistService.hasDenyListed(message)) {
            return MessageValidationResult.FAILED_DENYLISTED
        }

        if (containsLink(message)) {
            return MessageValidationResult.FAILED_BAD_LINK
        }

        if (!isValidInput(message)) {
            return MessageValidationResult.FAILED_BAD_CHARACTER
        }

        if (isSpamming(user.uuid)) {
            return MessageValidationResult.FAILED_SPAM
        }

        return MessageValidationResult.SUCCESS
    }

    override fun containsLink(message: Component): Boolean {
        val text = message.toPlainText()

        return urlRegex.findAll(text).any { result ->
            val rawUrl = result.value
            val formattedUrl = if (rawUrl.startsWith("http", ignoreCase = true)) rawUrl else "http://$rawUrl"

            val domain = try {
                URI(formattedUrl).host?.lowercase(Locale.getDefault())?.removePrefix("www.")
            } catch (e: Exception) {
                return@any true
            }

            domain == null || allowedDomains.none { domain.endsWith(it.lowercase(Locale.getDefault())) }
        }
    }


    override fun isValidInput(input: Component): Boolean {
        return validCharactersRegex.matches(input.toPlainText())
    }

    override fun isSpamming(uuid: UUID): Boolean {
        val currentTime = System.currentTimeMillis()
        val windowStart = currentTime - messageLimitSeconds * 1000
        val timestamps = messageTimestamps.getOrPut(uuid) { mutableListOf() }

        timestamps.removeIf { it < windowStart }

        if (timestamps.size >= messageLimitCount) {
            return true
        }

        timestamps.add(currentTime)
        return false
    }

    override fun setMessageLimit(milliseconds: Long, count: Int) {
        messageLimitSeconds = milliseconds
        messageLimitCount = count
    }

    override fun getMessageLimit(): Pair<Int, Long> {
        return messageLimitCount to messageLimitSeconds
    }

    override fun loadDomains(filterConfig: ChatFilterConfig) {
        val config = filterConfig as? FallbackChatFilterConfig ?: return
        allowedDomains.clear()
        allowedDomains.addAll(config.whitelistedDomains)
    }

    override fun loadMessageLimit(spamConfig: ChatSpamConfig) {
        val config = spamConfig as? FallbackChatSpamConfig ?: return
        messageLimitSeconds = config.timeFrame
        messageLimitCount = config.maxMessages
    }
}