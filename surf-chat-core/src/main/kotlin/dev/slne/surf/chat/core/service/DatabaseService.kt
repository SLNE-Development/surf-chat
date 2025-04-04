package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.UUID

interface DatabaseService {
    fun connect()

    suspend fun getUser(uuid: UUID): ChatUserModel
    suspend fun loadUser(uuid: UUID): ChatUserModel
    suspend fun saveUser(user: ChatUserModel)

    companion object {
        val INSTANCE = requiredService<DatabaseService>()
    }
}

val databaseService get() = DatabaseService.INSTANCE