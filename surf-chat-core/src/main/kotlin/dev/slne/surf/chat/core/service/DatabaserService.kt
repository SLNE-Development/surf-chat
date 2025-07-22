package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.nio.file.Path

interface DatabaseService {
    fun establishConnection(path: Path)
    fun createTables()
    fun closeConnection()

    companion object {
        val INSTANCE = requiredService<DatabaseService>()
    }
}

val databaseService get() = DatabaseService.INSTANCE