package dev.slne.surf.chat.fallback.repository.denylist

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.asCache
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistActionType
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.fallback.table.DenylistActionsTable
import dev.slne.surf.chat.fallback.table.DenylistTable
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.*
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import java.util.*
import kotlin.time.Duration.Companion.minutes

class DenyListRepositoryImpl : DenyListRepository {
    private val actionIDCache = Caffeine.newBuilder()
        .expireAfterWrite(5.minutes)
        .maximumSize(10_000)
        .asCache<String, ULong>()

    private suspend fun findActionIDByName(name: String): ULong? {
        val cached = actionIDCache.getIfPresent(name)
        if (cached != null) {
            return cached
        }

        val id = DenylistActionsTable
            .select(DenylistActionsTable.id)
            .where { DenylistActionsTable.name eq name }
            .singleOrNull()
            ?.let { it[DenylistActionsTable.id] }
            ?.value

        if (id != null) {
            actionIDCache.put(name, id)
        }

        return id
    }

    override suspend fun createDenylistEntry(
        word: String,
        reason: String,
        addedByUuid: UUID?,
        actionName: String
    ): Unit = suspendTransaction {
        val actionID = findActionIDByName(actionName) ?: error("Denylist action not found: $actionName")

        DenylistTable.insert {
            it[this.word] = word
            it[this.reason] = reason
            it[this.addedBy] = addedByUuid
            it[this.action] = actionID
        }
    }

    override suspend fun existsEntryByWord(word: String): Boolean = suspendTransaction {
        DenylistTable
            .selectAll()
            .where { DenylistTable.word eq word }
            .count() > 0
    }

    override suspend fun findEntryByWord(word: String): DenylistEntry? = suspendTransaction {
        (DenylistTable innerJoin DenylistActionsTable)
            .selectAll()
            .where { DenylistTable.word eq word }
            .singleOrNull()
            ?.let(::createEntryByRowWithAction)
    }

    override suspend fun findAllEntries(): List<DenylistEntry> = suspendTransaction {
        (DenylistTable innerJoin DenylistActionsTable)
            .selectAll()
            .map(::createEntryByRowWithAction)
            .toList()
    }

    override suspend fun createAction(
        name: String,
        type: DenylistActionType,
        reason: String,
        duration: Long
    ): Unit = suspendTransaction {
        DenylistActionsTable.insert {
            it[this.name] = name
            it[this.actionType] = type
            it[this.reason] = reason
            it[this.duration] = duration
        }
    }

    override suspend fun deleteActionByWord(word: String): Int = suspendTransaction {
        DenylistActionsTable.deleteWhere { DenylistTable.word eq word }
    }

    override suspend fun deleteAllActions(): Int = suspendTransaction {
        DenylistActionsTable.deleteAll()
    }

    override suspend fun existsActionByName(name: String): Boolean = suspendTransaction {
        DenylistActionsTable
            .selectAll()
            .where { DenylistActionsTable.name eq name }
            .count() > 0
    }

    override suspend fun findAllActions(): List<DenylistAction> = suspendTransaction {
        DenylistActionsTable
            .selectAll()
            .map(::createActionByRow)
            .toList()
    }

    override suspend fun deleteByWord(word: String): Int = suspendTransaction {
        DenylistTable.deleteWhere { DenylistTable.word eq word }
    }

    override suspend fun deleteAll() = suspendTransaction {
        DenylistTable.deleteAll()
    }

    companion object {
        fun createEntryByRowWithAction(fullRow: ResultRow) = DenylistEntry(
            word = fullRow[DenylistTable.word],
            addedBy = fullRow[DenylistTable.addedBy],
            reason = fullRow[DenylistTable.reason],
            addedAt = fullRow[DenylistTable.createdAt],
            action = createActionByRow(fullRow)
        )

        fun createActionByRow(row: ResultRow) = DenylistAction(
            name = row[DenylistActionsTable.name],
            actionType = row[DenylistActionsTable.actionType],
            reason = row[DenylistActionsTable.reason],
            duration = row[DenylistActionsTable.duration]
        )
    }
}