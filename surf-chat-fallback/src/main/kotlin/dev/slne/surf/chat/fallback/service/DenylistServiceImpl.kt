package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.core.service.DenylistService
import dev.slne.surf.chat.core.service.denylistActionService
import dev.slne.surf.chat.fallback.table.DenylistActionsTable
import dev.slne.surf.chat.fallback.table.DenylistTable
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectList
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import net.kyori.adventure.util.Services

@AutoService(DenylistService::class)
class DenylistServiceImpl : DenylistService, Services.Fallback {
    val entries = mutableObjectListOf<DenylistEntry>()

    override suspend fun addEntry(
        word: String,
        reason: String,
        addedBy: String,
        addedAt: Long,
        action: DenylistAction
    ) = suspendTransaction {
        val action =
            DenylistActionsTable.selectAll().where(DenylistActionsTable.name eq action.name)
                .map { row -> row[DenylistActionsTable.id] }
                .firstOrNull() ?: error("Denylist action not found: ${action.name}")

        DenylistTable.insert {
            it[this.word] = word
            it[this.reason] = reason
            it[this.addedBy] = addedBy
            it[this.addedAt] = addedAt
            it[this.action] = action.value
        }

        return@suspendTransaction
    }

    override fun addLocalEntry(
        word: String,
        reason: String,
        addedBy: String,
        addedAt: Long,
        action: DenylistAction
    ) {
        entries.add(
            DenylistEntry(
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

    override suspend fun clearEntries() = suspendTransaction {
        DenylistTable.deleteAll()
    }

    override fun getLocalEntries(): ObjectList<DenylistEntry> {
        return entries
    }

    override suspend fun removeEntry(word: String) = suspendTransaction {

        DenylistTable.deleteWhere {
            DenylistTable.word eq word
        }
        return@suspendTransaction
    }

    override suspend fun hasEntry(word: String) = suspendTransaction {
        DenylistTable.selectAll().where(DenylistTable.word eq word).map {
            it[DenylistTable.word]
        }.firstOrNull() != null
    }

    override suspend fun getEntry(word: String) = suspendTransaction {
        DenylistTable.selectAll().where(DenylistTable.word eq word).map {
            val denylistAction = denylistActionService.getActionById(it[DenylistTable.action])
                ?: error("Denylist action not found with id: ${it[DenylistTable.action]}")

            DenylistEntry(
                word = it[DenylistTable.word],
                reason = it[DenylistTable.reason],
                addedBy = it[DenylistTable.addedBy],
                addedAt = it[DenylistTable.addedAt],
                action = denylistAction
            )
        }.firstOrNull()
    }

    override suspend fun getEntries(): ObjectList<DenylistEntry> =
        suspendTransaction {
            DenylistTable.selectAll().map {
                val denylistAction = denylistActionService.getActionById(it[DenylistTable.action])
                    ?: error("Denylist action not found with id: ${it[DenylistTable.action]}")

                DenylistEntry(
                    word = it[DenylistTable.word],
                    reason = it[DenylistTable.reason],
                    addedBy = it[DenylistTable.addedBy],
                    addedAt = it[DenylistTable.addedAt],
                    action = denylistAction
                )
            }.toList().toObjectList()
        }

    override suspend fun fetch() = suspendTransaction {
        entries.clear()
        entries.addAll(getEntries())
        return@suspendTransaction
    }
}