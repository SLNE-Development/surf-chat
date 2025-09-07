package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.api.entry.DenylistEntry
import dev.slne.surf.chat.core.service.DenylistService
import dev.slne.surf.chat.fallback.entity.DenylistActionEntity
import dev.slne.surf.chat.fallback.entity.DenylistEntryEntity
import dev.slne.surf.chat.fallback.model.FallbackDenylistEntry
import dev.slne.surf.chat.fallback.table.DenylistActionsTable
import dev.slne.surf.chat.fallback.table.DenylistTable
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@AutoService(DenylistService::class)
class FallbackDenylistService : DenylistService, Services.Fallback {
    val entries = mutableObjectListOf<DenylistEntry>()

    override fun createTable() {
        transaction {
            SchemaUtils.create(DenylistTable, DenylistActionsTable)
        }
    }

    override suspend fun addEntry(
        word: String,
        reason: String,
        addedBy: String,
        addedAt: Long,
        action: DenylistAction
    ) = newSuspendedTransaction(Dispatchers.IO) {
        DenylistEntryEntity.new {
            this.word = word
            this.reason = reason
            this.addedBy = addedBy
            this.addedAt = addedAt
            this.action =
                DenylistActionEntity.find { DenylistActionsTable.name eq action.name }.firstOrNull()
                    ?: error("Denylist action not found: ${action.name}")
        }

        DenylistTable.insert {
            it[DenylistTable.word] = word
            it[DenylistTable.reason] = reason
            it[DenylistTable.addedBy] = addedBy
            it[DenylistTable.addedAt] = addedAt
        }

        return@newSuspendedTransaction
    }

    override fun addLocalEntry(
        word: String,
        reason: String,
        addedBy: String,
        addedAt: Long,
        action: DenylistAction
    ) {
        entries.add(
            FallbackDenylistEntry(
                word, reason, addedBy, addedAt, action
            )
        )
    }

    override fun removeLocalEntry(word: String) {
        entries.removeIf { it.word == word }
    }

    override fun hasLocalEntry(word: String): Boolean {
        return entries.any { it.word == word }
    }

    override fun getLocalEntry(word: String): DenylistEntry? {
        return entries.find { it.word == word }
    }

    override fun clearLocalEntries() {
        entries.clear()
    }

    override fun getLocalEntries(): ObjectList<DenylistEntry> {
        return entries
    }

    override suspend fun removeEntry(word: String) = newSuspendedTransaction(Dispatchers.IO) {
        DenylistTable.deleteWhere { DenylistTable.word eq word }
        return@newSuspendedTransaction
    }

    override suspend fun hasEntry(word: String) = newSuspendedTransaction(Dispatchers.IO) {
        val exists = DenylistTable.selectAll().where(DenylistTable.word eq word).count() > 0
        return@newSuspendedTransaction exists
    }

    override suspend fun getEntry(word: String) = newSuspendedTransaction(Dispatchers.IO) {
        DenylistEntryEntity.find(DenylistTable.word eq word).map {
            it.toDto()
        }.firstOrNull()
    }

    override suspend fun getEntries(): ObjectList<DenylistEntry> =
        newSuspendedTransaction(Dispatchers.IO) {
            DenylistEntryEntity.all().map {
                it.toDto()
            }.toObjectList()
        }

    override suspend fun fetch() = newSuspendedTransaction(Dispatchers.IO) {
        entries.clear()
        entries.addAll(
            DenylistEntryEntity.all().map {
                it.toDto()
            }
        )
        return@newSuspendedTransaction
    }
}