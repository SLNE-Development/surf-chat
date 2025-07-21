package dev.slne.surf.chat.fallback.entity

import dev.slne.surf.chat.api.entity.ConfigurableUser
import dev.slne.surf.chat.core.service.notificationService
import java.util.*

class FallbackConfigurableUser(override val uuid: UUID) : ConfigurableUser {
    override suspend fun pingsEnabled() = notificationService.pingsEnabled(uuid)
    override suspend fun enablePings() = notificationService.enablePings(uuid)
    override suspend fun disablePings() = notificationService.disablePings(uuid)
}