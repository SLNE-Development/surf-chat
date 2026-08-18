package dev.slne.surf.chat.minestom.service

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.messages.adventure.uuidOrNull
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.client.message.format.Components
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import dev.slne.surf.chat.core.common.service.DeletionService
import dev.slne.surf.chat.core.common.service.HistoryService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.kyori.adventure.audience.Audience

@AutoService(DeletionService::class)
class DeletionServiceImpl : DeletionService {
    override suspend fun deleteMessage(
        message: MessageData,
        deleter: Audience?,
        deletionReason: String?,
        notifyTeam: Boolean
    ): Boolean {
        val signature = message.signature ?: return false
        ChatPlatform.deleteMessage(signature)

        coroutineScope {
            launch {
                HistoryService.markDeleted(
                    message.messageUuid,
                    deleter?.uuidOrNull(),
                    deletionReason
                )
            }

            if (notifyTeam) {
                val notification =
                    Components.Deletion.createMessageDeletedTeamNotification(deleter, message)
                ChatPlatform.broadcast(notification, ChatPermissions.TEAM_NOTIFY_DELETION)
            }
        }

        return true
    }
}
