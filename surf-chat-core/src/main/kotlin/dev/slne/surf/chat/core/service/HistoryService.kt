package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService

interface HistoryService {

    companion object {
        val INSTANCE = requiredService<HistoryService>()
    }
}

val historyService get() = HistoryService.INSTANCE