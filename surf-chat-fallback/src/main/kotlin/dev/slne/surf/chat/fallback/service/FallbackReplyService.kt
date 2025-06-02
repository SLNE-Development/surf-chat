package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.ReplyService
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import net.kyori.adventure.util.Services
import java.util.UUID

@AutoService(ReplyService::class)
class FallbackReplyService : ReplyService, Services.Fallback {
    val replies = mutableObject2ObjectMapOf<UUID, UUID>()

    override fun updateLast(player: UUID, target: UUID) {
        replies[player] = target
    }

    override fun getLast(player: UUID): UUID? {
        return replies[player]
    }

    override fun clear(player: UUID) {
        replies.remove(player)
    }
}