package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService

interface DatabaseService {

    companion object {
        val INSTANCE = requiredService<DatabaseService>()
    }
}

val databaseService get() = DatabaseService.INSTANCE