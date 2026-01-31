package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.SpyService
import net.kyori.adventure.util.Services
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@AutoService(SpyService::class)
class SpyServiceImpl : SpyService, Services.Fallback {
    val privateMessageSpies = ConcurrentHashMap<UUID, MutableSet<UUID>>()

    override fun getPrivateMessageSpies(player: UUID) = privateMessageSpies.getOrDefault(player, emptySet())

    override fun addPrivateMessageSpy(player: UUID, target: UUID) = privateMessageSpies
        .computeIfAbsent(player) { ConcurrentHashMap.newKeySet() }
        .add(target)

    override fun removePrivateMessageSpy(player: UUID, target: UUID): Boolean {
        var removed = false

        privateMessageSpies.computeIfPresent(player) { _, spies ->
            removed = spies.remove(target)
            if (spies.isEmpty()) null else spies
        }

        return removed
    }

    override fun isPrivateMessageSpying(player: UUID) = privateMessageSpies.containsKey(player)

    override fun clearPrivateMessageSpies(player: UUID) {
        privateMessageSpies.remove(player)
    }

    override fun cleanup(player: UUID) {
        clearPrivateMessageSpies(player)
    }
}