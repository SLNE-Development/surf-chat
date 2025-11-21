package dev.slne.surf.chat.server.database.repository

import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.core.common.util.SyncValues
import dev.slne.surf.chat.server.database.entity.DenylistActionEntity
import dev.slne.surf.chat.server.database.entity.DenylistEntryEntity
import dev.slne.surf.chat.server.database.table.DenylistActionsTable
import dev.slne.surf.cloud.api.common.util.mutableObjectSetOf
import dev.slne.surf.cloud.api.server.plugin.CoroutineTransactional
import org.springframework.stereotype.Repository

@Repository
@CoroutineTransactional
class DenylistRepository {
    suspend fun cacheDenylist() {
        val entries = mutableObjectSetOf<DenylistEntry>()

        DenylistEntryEntity.all().forEach {
            entries.add(
                DenylistEntry(
                    it.word,
                    it.reason,
                    it.addedBy,
                    it.addedAt,
                    DenylistAction(
                        it.action.name,
                        it.action.actionType,
                        it.action.reason,
                        it.action.duration
                    )
                )
            )
        }

        SyncValues.denylistEntries.clear()
        SyncValues.denylistEntries.addAll(entries)
    }

    suspend fun storyDenylist() {
        DenylistEntryEntity.all().forEach { it.delete() }

        SyncValues.denylistEntries.forEach {
            DenylistEntryEntity.new {
                this.word = it.word
                this.reason = it.reason
                this.addedBy = it.addedBy
                this.addedAt = it.addedAt
                this.action =
                    DenylistActionEntity.find { DenylistActionsTable.name eq it.action.name }
                        .firstOrNull() ?: error("Denylist action not found: ${it.action.name}")
            }
        }
    }
}