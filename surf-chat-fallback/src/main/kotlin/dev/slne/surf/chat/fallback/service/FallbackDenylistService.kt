package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.entry.DenylistEntry
import dev.slne.surf.chat.core.service.DenylistService
import dev.slne.surf.chat.fallback.model.FallbackDenylistEntry
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@AutoService(DenylistService::class)
class FallbackDenylistService : DenylistService, Services.Fallback {
    object DenylistEntries : Table("chat_denylist_entries") {
        val index = integer("index").autoIncrement().uniqueIndex()
        val word = text("word").uniqueIndex()
        val reason = text("reason")
        val addedBy = varchar("added_by", 16)
        val addedAt = long("added_at")

        override val primaryKey = PrimaryKey(index)
    }

    val entries = mutableObjectListOf<DenylistEntry>()

    override fun createTable() {
        transaction {
            SchemaUtils.create(DenylistEntries)
        }
    }

    override suspend fun addEntry(
        word: String,
        reason: String,
        addedBy: String,
        addedAt: Long
    ) = newSuspendedTransaction(Dispatchers.IO) {
        DenylistEntries.insert {
            it[DenylistEntries.word] = word
            it[DenylistEntries.reason] = reason
            it[DenylistEntries.addedBy] = addedBy
            it[DenylistEntries.addedAt] = addedAt
        }

        return@newSuspendedTransaction
    }

    override fun addLocalEntry(
        word: String,
        reason: String,
        addedBy: String,
        addedAt: Long
    ) {
        entries.add(
            FallbackDenylistEntry(
                word, reason, addedBy, addedAt
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
        DenylistEntries.deleteWhere { DenylistEntries.word eq word }
        return@newSuspendedTransaction
    }

    override suspend fun hasEntry(word: String) = newSuspendedTransaction(Dispatchers.IO) {
        val exists = DenylistEntries.selectAll().where(DenylistEntries.word eq word).count() > 0
        return@newSuspendedTransaction exists
    }

    override suspend fun getEntry(word: String) = newSuspendedTransaction(Dispatchers.IO) {
        DenylistEntries.selectAll().where(DenylistEntries.word eq word).map {
            FallbackDenylistEntry(
                it[DenylistEntries.word],
                it[DenylistEntries.reason],
                it[DenylistEntries.addedBy],
                it[DenylistEntries.addedAt]
            )
        }.firstOrNull()
    }

    override suspend fun getEntries(): ObjectList<DenylistEntry> =
        newSuspendedTransaction(Dispatchers.IO) {
            DenylistEntries.selectAll().map {
                FallbackDenylistEntry(
                    it[DenylistEntries.word],
                    it[DenylistEntries.reason],
                    it[DenylistEntries.addedBy],
                    it[DenylistEntries.addedAt]
                )
            }.toObjectList()
        }

    override suspend fun fetch() = newSuspendedTransaction(Dispatchers.IO) {
        entries.clear()
        entries.addAll(
            DenylistEntries.selectAll().map {
                FallbackDenylistEntry(
                    it[DenylistEntries.word],
                    it[DenylistEntries.reason],
                    it[DenylistEntries.addedBy],
                    it[DenylistEntries.addedAt]
                )
            }
        )
        return@newSuspendedTransaction
    }
}