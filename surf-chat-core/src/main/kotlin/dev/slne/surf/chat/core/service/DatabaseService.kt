package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.UUID

interface DatabaseService {
    fun connect()

    fun getUser(uuid: UUID): ChatUserModel
    fun loadUser(uuid: UUID): ChatUserModel
    fun saveUser(uuid: UUID)

    companion object {
        val INSTANCE = requiredService<DatabaseService>()
    }
}

val databaseService get() = DatabaseService.INSTANCE