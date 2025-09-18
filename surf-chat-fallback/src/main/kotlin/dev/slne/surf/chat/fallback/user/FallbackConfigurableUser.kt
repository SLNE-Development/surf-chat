package dev.slne.surf.chat.fallback.user

import dev.slne.surf.chat.api.entity.ConfigurableUser
import dev.slne.surf.chat.core.service.directMessageService
import dev.slne.surf.chat.core.service.notificationService
import java.util.*

class FallbackConfigurableUser(override val uuid: UUID) : ConfigurableUser {
    override suspend fun pingsEnabled() = notificationService.pingsEnabled(uuid)
    override suspend fun enablePings() = notificationService.enablePings(uuid)
    override suspend fun disablePings() = notificationService.disablePings(uuid)

    override suspend fun directMessagesEnabled() = directMessageService.directMessagesEnabled(uuid)
    override suspend fun enableDirectMessages() = directMessageService.enableDirectMessages(uuid)
    override suspend fun disableDirectMessages() = directMessageService.disableDirectMessages(uuid)

    override suspend fun invitesEnabled() = notificationService.invitesEnabled(uuid)
    override suspend fun enableInvites() = notificationService.enableInvites(uuid)
    override suspend fun disableInvites() = notificationService.disableInvites(uuid)
}