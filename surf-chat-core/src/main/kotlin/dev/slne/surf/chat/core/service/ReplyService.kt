package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

interface ReplyService {
    fun updateLast(player: UUID, target: UUID)
    fun getLast(player: UUID): UUID?
    fun clear(player: UUID)

    companion object {
        val INSTANCE = requiredService<ReplyService>()
    }
}

val replyService get() = ReplyService.INSTANCE