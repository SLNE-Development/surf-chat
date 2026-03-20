package dev.slne.surf.chat.microservice.repository.functionality

import dev.slne.surf.chat.core.functionality.Functionalities
import dev.slne.surf.chat.microservice.table.FunctionalityTable
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insertIgnore
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.toList

class FunctionalityRepositoryImpl : FunctionalityRepository {
    override suspend fun findByServerOrCreate(server: String): Functionalities = suspendTransaction {
        FunctionalityTable.insertIgnore { // ensure row exists
            it[this.server] = server
            it[this.chatEnabled] = Functionalities.EMPTY.localChatEnabled
        }

        FunctionalityTable.selectAll()
            .where { FunctionalityTable.server eq server }
            .single()
            .let(::createByRow)
    }

    override suspend fun updateOrCreate(server: String, functionalities: Functionalities): Unit = suspendTransaction {
        FunctionalityTable.upsert {
            it[this.server] = server
            it[this.chatEnabled] = functionalities.localChatEnabled
        }
    }

    override suspend fun findAll(): Map<String, Functionalities> = suspendTransaction {
        FunctionalityTable.selectAll()
            .map { row -> row[FunctionalityTable.server] to createByRow(row) }
            .toList()
            .toMap()
    }

    companion object {
        fun createByRow(row: ResultRow) = Functionalities(
            localChatEnabled = row[FunctionalityTable.chatEnabled]
        )
    }
}