package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.core.service.DenylistActionService
import dev.slne.surf.chat.fallback.repository.denylist.DenyListRepository
import io.ktor.util.collections.*
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import net.kyori.adventure.util.Services

@AutoService(DenylistActionService::class)
class DenylistActionServiceImpl : DenylistActionService, Services.Fallback {
    private val cache = ConcurrentMap<String, DenylistAction>()

    override suspend fun addAction(action: DenylistAction) {
        DenyListRepository.createAction(action.name, action.actionType, action.reason, action.duration)
    }

    override suspend fun removeAction(action: DenylistAction) {
        DenyListRepository.deleteActionByName(action.name)
    }

    override suspend fun hasAction(name: String): Boolean {
        return DenyListRepository.existsActionByName(name)
    }

    override suspend fun fetchActions() {
        val newEntries = DenyListRepository.findAllActions().associateBy { it.name }
        cache.clear()
        cache.putAll(newEntries)
    }

    override fun addLocalAction(action: DenylistAction) = cache.putIfAbsent(action.name, action) == null
    override fun removeLocalAction(action: DenylistAction) = cache.remove(action.name) != null
    override fun getLocalAction(name: String) = cache[name]

    override fun listLocalActions() = ObjectArraySet(cache.values)
    override fun hasLocalAction(name: String) = cache.containsKey(name)
    override suspend fun clearActions(): Int {
        return DenyListRepository.deleteAllActions()
    }

    override fun clearLocalActions() = cache.clear()
}