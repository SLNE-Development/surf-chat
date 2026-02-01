package dev.slne.surf.chat.fallback.repository.denylist

import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistActionType
import dev.slne.surf.chat.api.denylist.DenylistEntry
import java.util.*

interface DenyListRepository {
    suspend fun createDenylistEntry(word: String, reason: String, addedByUuid: UUID?, actionName: String)

    suspend fun existsEntryByWord(word: String): Boolean
    suspend fun findEntryByWord(word: String): DenylistEntry?
    suspend fun findAllEntries(): List<DenylistEntry>

    suspend fun createAction(name: String, type: DenylistActionType, reason: String, duration: Long)
    suspend fun deleteActionByName(name: String): Int
    suspend fun deleteAllActions(): Int
    suspend fun existsActionByName(name: String): Boolean
    suspend fun findAllActions(): List<DenylistAction>

    suspend fun deleteByWord(word: String): Int
    suspend fun deleteAll(): Int

    companion object : DenyListRepository by DenyListRepositoryImpl()
}