package dev.slne.surf.chat.paper.service

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.messages.adventure.uuidOrNull
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.client.message.format.Components
import dev.slne.surf.chat.core.common.service.DeletionService
import dev.slne.surf.chat.core.common.service.HistoryService
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.audience.Audience
import org.bukkit.Bukkit

@AutoService(DeletionService::class)
class DeletionServiceImpl : DeletionService {
    override suspend fun deleteMessage(
        message: MessageData,
        deleter: Audience?,
        deletionReason: String?,
        notifyTeam: Boolean
    ): Boolean {
        val signature = message.signature ?: return false
        server.deleteMessage(signature)

        coroutineScope {
            launch {
                HistoryService.markDeleted(message.messageUuid, deleter?.uuidOrNull(), deletionReason)
            }

            if (notifyTeam) {
                val message = Components.Deletion.createMessageDeletedTeamNotification(deleter, message)
                Bukkit.broadcast(message, PermissionRegistry.TEAM_NOTIFY_DELETION)
            }
        }

        return true
    }
}