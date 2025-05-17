package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.type.MessageValidationResult
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.toPlainText
import dev.slne.surf.chat.core.service.FilterService
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.entity.Player
import java.net.URI
import java.util.*

@AutoService(FilterService::class)
class BukkitFilterService : FilterService, Fallback {
    private val allowedDomains = mutableObjectSetOf<String>()

    private val messageTimestamps = mutableMapOf<UUID, MutableList<Long>>()

    private val validCharactersRegex = "^[\\u0000-\\u007FäöüÄÖÜß€@£¥|²³µ½¼¾«»¡¿°§´`^~¨]+$".toRegex()
    private val urlRegex =
        "((http|https|ftp)://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?".toRegex(RegexOption.IGNORE_CASE)

    private var messageLimitSeconds = 10
    private var messageLimitCount = 5

    override fun find(message: Component, sender: Player): MessageValidationResult {
        if (sender.hasPermission(ChatPermissionRegistry.FILTER_BYPASS)) {
            return MessageValidationResult.SUCCESS
        }

        if (denylistService.hasDenyListed(message)) {
            return MessageValidationResult.FAILED_DENYLISTED
        }

        if (containsLink(message)) {
            return MessageValidationResult.FAILED_BAD_LINK
        }

        if (!isValidInput(message)) {
            return MessageValidationResult.FAILED_BAD_CHARACTER
        }

        if (isSpamming(sender.uniqueId)) {
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

    override fun setMessageLimit(seconds: Int, count: Int) {
        messageLimitSeconds = seconds
        messageLimitCount = count
    }

    override fun getMessageLimit(): Pair<Int, Int> {
        return messageLimitCount to messageLimitSeconds
    }

    override fun loadDomains() {
        val config = plugin.config
        allowedDomains.clear()
        allowedDomains.addAll(config.getStringList("whitelisted-domains"))
    }

    override fun loadMessageLimit() {
        val config = plugin.config
        messageLimitSeconds = config.getInt("spam-protection.in-seconds", 10)
        messageLimitCount = config.getInt("spam-protection.max-messages", 5)
    }

    override fun saveMessageLimit() {
        val config = plugin.config

        config.set("spam-protection.in-seconds", messageLimitSeconds)
        config.set("spam-protection.max-messages", messageLimitCount)

        plugin.saveConfig()
    }
}
