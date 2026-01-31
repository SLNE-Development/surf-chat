package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.core.service.DenylistService
import dev.slne.surf.chat.fallback.repository.denylist.DenyListRepository
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import net.kyori.adventure.util.Services
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@AutoService(DenylistService::class)
class DenylistServiceImpl : DenylistService, Services.Fallback {
    private val cache = ConcurrentHashMap<String, DenylistEntry>()

    override suspend fun addEntry(
        word: String,
        reason: String,
        addedByUuid: UUID?,
        action: DenylistAction
    ) {
        DenyListRepository.createDenylistEntry(word, reason, addedByUuid, action.name)
    }

    override fun addLocalEntry(
        word: String,
        reason: String,
        addedBy: UUID?,
        addedAt: OffsetDateTime,
        action: DenylistAction
    ) {
        val entry = DenylistEntry(word, reason, addedBy, addedAt, action)
        val old = cache.put(word, entry)
        if (old != null) {
            error("Denylist entry already exists: $word")
        }
    }

    override fun removeLocalEntry(word: String) {
        cache.remove(word)
    }

    override fun hasLocalEntry(word: String): Boolean {
        return cache.containsKey(word)
    }

    override fun getLocalEntry(word: String): DenylistEntry? {
        return cache[word]
    }

    override fun clearLocalEntries() {
        cache.clear()
    }

    override suspend fun clearEntries(): Int {
        return DenyListRepository.deleteAll()
    }

    override fun getLocalEntries(): ObjectList<DenylistEntry> {
        return ObjectArrayList(cache.values)
    }

    override suspend fun removeEntry(word: String) {
        DenyListRepository.deleteByWord(word)
    }

    override suspend fun hasEntry(word: String): Boolean {
        return DenyListRepository.existsEntryByWord(word)
    }

    override suspend fun getEntry(word: String): DenylistEntry? {
        return DenyListRepository.findEntryByWord(word)
    }

    override suspend fun getEntries(): ObjectList<DenylistEntry> {
        return DenyListRepository.findAllEntries().toObjectList()
    }

    override suspend fun fetch() {
        val newEntries = getEntries().associateBy { it.word }
        clearLocalEntries()
        cache.putAll(newEntries)
    }
}