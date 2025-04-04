package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService

interface FilterService {

    companion object {
        val INSTANCE = requiredService<FilterService>()
    }
}

val filterService get() = FilterService.INSTANCE