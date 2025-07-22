package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

interface NotificationService : ServiceUsingDatabase {
    suspend fun pingsEnabled(uuid: UUID): Boolean
    suspend fun enablePings(uuid: UUID)
    suspend fun disablePings(uuid: UUID)

    companion object {
        val INSTANCE = requiredService<NotificationService>()
    }
}

val notificationService get() = NotificationService.INSTANCE