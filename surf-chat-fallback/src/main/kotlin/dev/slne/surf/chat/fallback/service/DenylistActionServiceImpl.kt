package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.core.service.DenylistActionService
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.fallback.repository.denylist.DenyListRepository
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import io.ktor.util.collections.*
import it.unimi.dsi.fastutil.objects.ObjectArraySet
import kotlinx.coroutines.delay
import net.kyori.adventure.chat.SignedMessage
import net.kyori.adventure.util.Services
import java.util.*
import kotlin.time.Duration.Companion.seconds

@AutoService(DenylistActionService::class)
class DenylistActionServiceImpl : DenylistActionService, Services.Fallback {
    private val cache = ConcurrentMap<String, DenylistAction>()

    override suspend fun addAction(action: DenylistAction) {
        DenyListRepository.createAction(action.name, action.actionType, action.reason, action.duration)
    }

    override suspend fun removeAction(action: DenylistAction) {
        DenyListRepository.deleteActionByWord(action.name)
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

    override suspend fun makeAction(
        messageUuid: UUID,
        entry: DenylistEntry,
        message: SignedMessage,
        sender: User,
        discordHookUrl: String?
    ) {
        delay(3.seconds)
        server.deleteMessage(message)
        historyService.markDeleted(messageUuid, "Arty Support (BLOCKED: ${entry.word})")
    }
}