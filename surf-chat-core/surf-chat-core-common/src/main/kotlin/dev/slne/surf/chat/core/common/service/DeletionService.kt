package dev.slne.surf.chat.core.common.service

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.chat.api.message.MessageData
import net.kyori.adventure.audience.Audience

private val service = requiredService<DeletionService>()

interface DeletionService {
    suspend fun deleteMessage(
        message: MessageData,
        deleter: Audience? = null,
        deletionReason: String? = null,
        notifyTeam: Boolean = true
    ): Boolean

    companion object : DeletionService by service
}