package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.type.MessageValidationResult
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.toPlainText
import dev.slne.surf.chat.core.service.FilterService
import dev.slne.surf.chat.core.service.blacklistService
import dev.slne.surf.surfapi.core.api.util.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.entity.Player
import java.util.*

@AutoService(FilterService::class)
class BukkitFilterService : FilterService, Fallback {
    private val allowedDomains = mutableObjectSetOf<String>()

    private val messageTimestamps = mutableMapOf<UUID, MutableList<Long>>()

    private val validCharactersRegex = "^[a-zA-Z0-9/.:_,()%&=?!<>|#^\"²³+*~\\-äöüß@\\[\\] ]*$".toRegex()
    private val urlRegex = "((http|https|ftp)://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?".toRegex(RegexOption.IGNORE_CASE)

    private var messageLimitSeconds = 10
    private var messageLimitCount = 5

    override fun find(message: Component, sender: Player): MessageValidationResult {
        if (sender.hasPermission("surf.chat.filter.bypass")) {
            return MessageValidationResult.SUCCESS
        }

        if (blacklistService.hasBlackListed(message)) {
            return MessageValidationResult.FAILED_BLACKLIST
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
        return urlRegex.findAll(message.toPlainText()).any { result ->
            val domain = result.groupValues.getOrNull(3) ?: return@any false
            allowedDomains.none { domain.endsWith(it) }
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
