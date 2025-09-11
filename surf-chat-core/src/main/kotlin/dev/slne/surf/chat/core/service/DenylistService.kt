package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.api.entry.DenylistEntry
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList

interface DenylistService : DatabaseTableHolder {
    suspend fun addEntry(
        word: String,
        reason: String,
        addedBy: String,
        addedAt: Long,
        action: DenylistAction
    )

    fun addLocalEntry(
        word: String,
        reason: String,
        addedBy: String,
        addedAt: Long,
        action: DenylistAction
    )

    fun removeLocalEntry(word: String)
    fun hasLocalEntry(word: String): Boolean
    fun getLocalEntry(word: String): DenylistEntry?
    fun clearLocalEntries()
    fun getLocalEntries(): ObjectList<DenylistEntry>

    suspend fun removeEntry(word: String)
    suspend fun hasEntry(word: String): Boolean
    suspend fun getEntry(word: String): DenylistEntry?
    suspend fun getEntries(): ObjectList<DenylistEntry>

    suspend fun fetch()

    companion object {
        val INSTANCE = requiredService<DenylistService>()
    }
}

val denylistService get() = DenylistService.INSTANCE