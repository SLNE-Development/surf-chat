package dev.slne.surf.chat.core

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.nio.file.Path

val databaseLoader = requiredService<DatabaseLoader>()

interface DatabaseLoader {
    suspend fun connect(dataPath: Path)
    fun disconnect()
}