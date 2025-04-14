package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.type.MessageValidationResult
import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface FilterService {
    /**
     *
     * Checks if the message is valid.
     * @param message The message to check.
     * @param sender The player who sent the message.
     * @return The validation error if the message is invalid, or MessageValidationError.SUCCESS if the message is valid.
     */
    fun find(message: Component, sender: Player): MessageValidationResult

    companion object {
        val INSTANCE = requiredService<FilterService>()
    }
}

val filterService get() = FilterService.INSTANCE