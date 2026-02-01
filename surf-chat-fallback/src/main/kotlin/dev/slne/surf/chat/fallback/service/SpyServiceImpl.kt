package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.SpyService
import net.kyori.adventure.util.Services
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@AutoService(SpyService::class)
class SpyServiceImpl : SpyService, Services.Fallback {
    private val privateMessageSpies = ConcurrentHashMap<UUID, MutableSet<UUID>>()
    private val observedToSpies = ConcurrentHashMap<UUID, MutableSet<UUID>>()

    override fun getObservingPlayers(observed: UUID) = observedToSpies[observed] ?: emptySet()

    override fun addPrivateMessageSpy(spy: UUID, observed: UUID): Boolean {
        val added = privateMessageSpies
            .computeIfAbsent(spy) { ConcurrentHashMap.newKeySet() }
            .add(observed)

        if (added) {
            observedToSpies
                .computeIfAbsent(observed) { ConcurrentHashMap.newKeySet() }
                .add(spy)
        }

        return added
    }

    override fun removePrivateMessageSpy(spy: UUID, observed: UUID): Boolean {
        var removed = false

        privateMessageSpies.computeIfPresent(spy) { _, spies ->
            removed = spies.remove(observed)
            if (spies.isEmpty()) null else spies
        }

        if (removed) {
            observedToSpies.computeIfPresent(observed) { _, spies ->
                spies.remove(spy)
                if (spies.isEmpty()) null else spies
            }
        }

        return removed
    }

    override fun isPrivateMessageSpying(spy: UUID) = privateMessageSpies.containsKey(spy)

    override fun clearPrivateMessageSpies(spy: UUID) {
        privateMessageSpies.remove(spy)?.forEach { observed ->
            observedToSpies.computeIfPresent(observed) { _, spies ->
                spies.remove(spy)
                if (spies.isEmpty()) null else spies
            }
        }
    }

    override fun cleanup(player: UUID) {
        clearPrivateMessageSpies(player)
        observedToSpies.remove(player)?.forEach { spy ->
            privateMessageSpies.computeIfPresent(spy) { _, targets ->
                targets.remove(player)
                if (targets.isEmpty()) null else targets
            }
        }
    }
}