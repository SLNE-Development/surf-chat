package dev.slne.surf.chat.bukkit.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.ReplyService
import net.kyori.adventure.util.Services.Fallback
import java.util.*

@AutoService(ReplyService::class)
class BukkitReplyService() : ReplyService, Fallback {
    private val cache = Caffeine.newBuilder()
        .maximumSize(1000)
        .build<UUID, UUID>()

    override fun updateLast(player: UUID, target: UUID) {
        cache.put(player, target)
    }

    override fun getLast(player: UUID): UUID? {
        return cache.getIfPresent(player)
    }

    override fun clear(player: UUID) {
        cache.invalidate(player)
    }
}