package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

interface DirectMessageService : ServiceUsingDatabase {
    suspend fun directMessagesEnabled(uuid: UUID): Boolean
    suspend fun enableDirectMessages(uuid: UUID)
    suspend fun disableDirectMessages(uuid: UUID)

    companion object {
        val INSTANCE = requiredService<DirectMessageService>()
    }
}

val directMessageService get() = DirectMessageService.INSTANCE