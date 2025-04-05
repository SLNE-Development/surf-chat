package dev.slne.surf.chat.bukkit.service

import dev.slne.surf.chat.core.service.FilterService
import dev.slne.surf.surfapi.core.api.util.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import java.util.*
import kotlin.time.Duration.Companion.seconds

private const val MESSAGE_LIMIT = 5

class BukkitFilterService(): FilterService {
    override fun find(message: Component): Boolean {
        TODO("Not yet implemented")
    }

    private val allowedDomains = mutableObjectSetOf<String>()

    private val blockedPatterns = mutableObjectSetOf<Regex>()
    private val rateLimit = mutableObject2LongMapOf<UUID>().apply { defaultReturnValue(0) }.synchronize()
    private val messageCount = mutableObject2IntMapOf<UUID>().apply { defaultReturnValue(0) }.synchronize()

    private val validCharactersRegex = "^[a-zA-Z0-9/.:_,()%&=?!<>|#^\"²³+*~-äöü@ ]*$".toRegex()
    private val urlRegex = "((http|https|ftp)://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?".toRegex(RegexOption.IGNORE_CASE)


    fun containsBlocked(message: Component) = blockedPatterns.any {
        it.containsMatchIn(
            PlainTextComponentSerializer.plainText().serialize(message)
        )
    }


    fun containsLink(message: Component): Boolean {
        val plainMessage = PlainTextComponentSerializer.plainText().serialize(message)

        return urlRegex.findAll(plainMessage).any { result ->
            val domain = result.groupValues.getOrNull(3) ?: return@any false
            allowedDomains.none { domain.endsWith(it) }
        }
    }

    fun isValidInput(input: Component): Boolean {
        return validCharactersRegex.matches(PlainTextComponentSerializer.plainText().serialize(input))
    }

    fun isSpamming(uuid: UUID): Boolean {
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