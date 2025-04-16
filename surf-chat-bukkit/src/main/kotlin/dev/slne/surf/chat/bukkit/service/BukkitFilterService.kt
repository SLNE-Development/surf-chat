package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.type.MessageValidationResult
import dev.slne.surf.chat.bukkit.util.toPlainText
import dev.slne.surf.chat.core.service.FilterService
import dev.slne.surf.chat.core.service.blacklistService
import dev.slne.surf.surfapi.core.api.util.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.entity.Player
import java.util.*
import kotlin.time.Duration.Companion.seconds

private const val MESSAGE_LIMIT = 5

@AutoService(FilterService::class)
class BukkitFilterService(): FilterService, Fallback {
    private val allowedDomains = mutableObjectSetOf<String>()
    private val rateLimit = mutableObject2LongMapOf<UUID>().apply { defaultReturnValue(0) }.synchronize()
    private val messageCount = mutableObject2IntMapOf<UUID>().apply { defaultReturnValue(0) }.synchronize()
    private val validCharactersRegex = "^[a-zA-Z0-9/.:_,()%&=?!<>|#^\"²³+*~\\-äöü@\\[\\] ]*$".toRegex()
    private val urlRegex = "((http|https|ftp)://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?".toRegex(RegexOption.IGNORE_CASE)

    override fun find(message: Component, sender: Player): MessageValidationResult {
        if(sender.hasPermission("surf.chat.filter.bypass")) {
            return MessageValidationResult.SUCCESS
        }


        if(blacklistService.hasBlackListed(message)) {
            return MessageValidationResult.FAILED_BLACKLIST
        }

        if(containsLink(message)) {
            return MessageValidationResult.FAILED_BAD_LINK
        }

        if(!isValidInput(message)) {
            return MessageValidationResult.FAILED_BAD_CHARACTER
        }

        if(isSpamming(sender.uniqueId)) {
            return MessageValidationResult.FAILED_SPAM
        }

        return MessageValidationResult.SUCCESS
    }

    private fun containsLink(message: Component): Boolean {
        return urlRegex.findAll(message.toPlainText()).any { result ->
            val domain = result.groupValues.getOrNull(3) ?: return@any false
            allowedDomains.none { domain.endsWith(it) }
        }
    }

    private fun isValidInput(input: Component): Boolean {
        return validCharactersRegex.matches(input.toPlainText())
    }

    private fun isSpamming(uuid: UUID): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastMessageTime = rateLimit.getLong(uuid)
        val count = messageCount.getInt(uuid)

        return if (currentTime - lastMessageTime < 10.seconds.inWholeMilliseconds) {
            (count >= MESSAGE_LIMIT).also { if (!it) messageCount[uuid] = count + 1 }
        } else {
            rateLimit[uuid] = currentTime
            messageCount[uuid] = 1
            false
        }
    }
}