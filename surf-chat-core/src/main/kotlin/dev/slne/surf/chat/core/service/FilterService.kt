package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.type.MessageValidationError
import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

interface FilterService {
    fun find(message: Component, sender: Player): MessageValidationError

    companion object {
        val INSTANCE = requiredService<FilterService>()
    }
}

val filterService get() = FilterService.INSTANCE