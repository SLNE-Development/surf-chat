package dev.slne.surf.chat.microservice.repository.ignore

import dev.slne.surf.chat.api.entry.IgnoreListEntry
import dev.slne.surf.chat.microservice.table.IgnoreListTable
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.ExposedR2dbcException
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.utils.asDataIntegrityViolation
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.util.*

object IgnoreRepository {
    suspend fun ignore(uuid: UUID, ignoredUUID: UUID): Boolean = suspendTransaction {
        try {
            IgnoreListTable.insert {
                it[this.userUuid] = uuid
                it[this.ignoredUuid] = ignoredUUID
            }
            true
        } catch (e: ExposedR2dbcException) {
            e.asDataIntegrityViolation()
            false
        }
    }

    suspend fun unignore(uuid: UUID, ignoredUUID: UUID): Boolean = suspendTransaction {
        IgnoreListTable.deleteWhere {
            (IgnoreListTable.userUuid eq uuid) and (IgnoreListTable.ignoredUuid eq ignoredUUID)
        } > 0
    }

    suspend fun findAllByUuid(uuid: UUID): List<IgnoreListEntry> = suspendTransaction {
        IgnoreListTable.selectAll()
            .where { IgnoreListTable.userUuid eq uuid }
            .map { row ->
                IgnoreListEntry(
                    user = row[IgnoreListTable.userUuid],
                    target = row[IgnoreListTable.ignoredUuid],
                    createdAt = row[IgnoreListTable.createdAt]
                )
            }
            .toList()
    }
}