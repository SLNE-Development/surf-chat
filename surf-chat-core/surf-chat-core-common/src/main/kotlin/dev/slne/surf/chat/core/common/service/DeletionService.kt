package dev.slne.surf.chat.core.common.service

import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.audience.Audience

interface DeletionService {

    suspend fun deleteMessage(
        message: MessageData,
        deleter: Audience? = null,
        deletionReason: String? = null,
        notifyTeam: Boolean = true
    ): Boolean

    companion object {
        val INSTANCE = requiredService<DeletionService>()
    }
}

val deletionService get() = DeletionService.INSTANCE