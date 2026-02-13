package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.util.Components
import dev.slne.surf.chat.bukkit.util.uuidOrNull
import dev.slne.surf.chat.core.service.DeletionService
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.bukkit.api.extensions.server
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
                historyService.markDeleted(message.messageUuid, deleter?.uuidOrNull(), deletionReason)
            }

            if (notifyTeam) {
                val message = Components.Deletion.createMessageDeletedTeamNotification(deleter, message)
                Bukkit.broadcast(message, PermissionRegistry.TEAM_NOTIFY_DELETION)
            }
        }

        return true
    }
}