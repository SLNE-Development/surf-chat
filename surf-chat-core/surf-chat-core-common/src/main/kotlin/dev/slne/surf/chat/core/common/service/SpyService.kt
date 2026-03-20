package dev.slne.surf.chat.core.common.service

import java.util.*
import java.util.concurrent.ConcurrentHashMap


object SpyService {
    private val privateMessageSpies = ConcurrentHashMap<UUID, MutableSet<UUID>>()
    private val observedToSpies = ConcurrentHashMap<UUID, MutableSet<UUID>>()

    fun getObservingPlayers(observed: UUID) = observedToSpies[observed] ?: emptySet()

    fun addPrivateMessageSpy(spy: UUID, observed: UUID): Boolean {
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

    fun removePrivateMessageSpy(spy: UUID, observed: UUID): Boolean {
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

    fun isPrivateMessageSpying(spy: UUID) = privateMessageSpies.containsKey(spy)

    fun clearPrivateMessageSpies(spy: UUID) {
        privateMessageSpies.remove(spy)?.forEach { observed ->
            observedToSpies.computeIfPresent(observed) { _, spies ->
                spies.remove(spy)
                if (spies.isEmpty()) null else spies
            }
        }
    }

    fun cleanup(player: UUID) {
        clearPrivateMessageSpies(player)
        observedToSpies.remove(player)?.forEach { spy ->
            privateMessageSpies.computeIfPresent(spy) { _, targets ->
                targets.remove(player)
                if (targets.isEmpty()) null else targets
            }
        }
    }
}