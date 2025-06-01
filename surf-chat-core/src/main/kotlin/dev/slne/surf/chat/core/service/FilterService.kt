package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.api.type.MessageValidationResult
import dev.slne.surf.chat.core.service.config.ChatFilterConfig
import dev.slne.surf.chat.core.service.config.ChatSpamConfig
import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component
import java.util.*

interface FilterService {
    /**
     *
     * Checks if the message is valid.
     * @param message The message to check.
     * @param user The player who sent the message.
     * @return The validation error if the message is invalid, or MessageValidationError.SUCCESS if the message is valid.
     */
    fun find(message: Component, user: ChatUser): MessageValidationResult

    fun containsLink(message: Component): Boolean
    fun isValidInput(input: Component): Boolean
    fun isSpamming(uuid: UUID): Boolean

    fun setMessageLimit(milliseconds: Long, count: Int)
    fun getMessageLimit(): Pair<Int, Long>

    fun loadDomains(filterConfig: ChatFilterConfig)

    fun loadMessageLimit(spamConfig: ChatSpamConfig)

    companion object {
        val INSTANCE = requiredService<FilterService>()
    }
}

val filterService get() = FilterService.INSTANCE