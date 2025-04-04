package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component

interface FilterService {
    fun find(message: Component): Boolean

    companion object {
        val INSTANCE = requiredService<FilterService>()
    }
}

val filterService get() = FilterService.INSTANCE