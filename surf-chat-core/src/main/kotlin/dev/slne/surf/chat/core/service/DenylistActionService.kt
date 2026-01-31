package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.chat.SignedMessage
import java.util.*

interface DenylistActionService {
    suspend fun addAction(action: DenylistAction)
    suspend fun removeAction(action: DenylistAction)
    suspend fun hasAction(name: String): Boolean
    suspend fun clearActions(): Int
    suspend fun fetchActions()
    suspend fun deleteMessage(
        messageUuid: UUID,
        entry: DenylistEntry,
        message: SignedMessage,
        deletedBy: UUID,
        deletionReason: String?,
        discordHookUrl: String?
    )

    fun addLocalAction(action: DenylistAction): Boolean
    fun removeLocalAction(action: DenylistAction): Boolean
    fun getLocalAction(name: String): DenylistAction?
    fun listLocalActions(): ObjectSet<DenylistAction>
    fun hasLocalAction(name: String): Boolean
    fun clearLocalActions()

    companion object {
        val INSTANCE = requiredService<DenylistActionService>()
    }
}

/**
 *
 */
val denylistActionService get() = DenylistActionService.INSTANCE