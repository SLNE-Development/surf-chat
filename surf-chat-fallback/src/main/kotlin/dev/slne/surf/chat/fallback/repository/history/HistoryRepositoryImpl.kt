package dev.slne.surf.chat.fallback.repository.history

import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageType
import dev.slne.surf.chat.fallback.table.HistoryTable
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.greaterEq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.like
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.neq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.andWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.update
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import java.time.OffsetDateTime
import java.util.*

class HistoryRepositoryImpl : HistoryRepository {
    override suspend fun createHistoryEntry(
        messageUuid: UUID,
        senderUuid: UUID,
        receiverUuid: UUID?,
        message: String,
        sentAt: OffsetDateTime,
        type: MessageType,
        server: String
    ): Unit = suspendTransaction {
        HistoryTable.insert {
            it[this.messageUuid] = messageUuid
            it[this.senderUuid] = senderUuid
            it[this.receiverUuid] = receiverUuid
            it[this.message] = message
            it[this.sentAt] = sentAt
            it[this.type] = type
            it[this.server] = server
        }
    }

    override suspend fun findHistories(filter: HistoryFilter): Set<HistoryEntry> = suspendTransaction {
        val query = HistoryTable.selectAll()
            .limit(filter.limit)

        filter.senderUuid?.let { query.andWhere { HistoryTable.senderUuid eq it } }
        filter.receiverUuid?.let { query.andWhere { HistoryTable.receiverUuid eq it } }
        filter.messageType?.let { query.andWhere { HistoryTable.type eq it } }
        filter.after?.let { query.andWhere { HistoryTable.sentAt greaterEq it } }
        filter.messageLike?.let { query.andWhere { HistoryTable.message like "%$it%" } }
        filter.deletedBy?.let { query.andWhere { HistoryTable.deletedBy eq it } }
        filter.deleted?.let {
            query.andWhere {
                if (it) {
                    HistoryTable.deletedAt neq null
                } else {
                    HistoryTable.deletedAt eq null
                }
            }
        }
        filter.server?.let { query.andWhere { HistoryTable.server eq it } }
        filter.messageUuid?.let { query.andWhere { HistoryTable.messageUuid eq it } }

        query.map(::createByRow).toSet()
    }

    override suspend fun markDeleted(
        messageUuid: UUID,
        deletedBy: UUID?,
        deletionReason: String?,
        deletedAt: OffsetDateTime
    ): Unit = suspendTransaction {
        HistoryTable.update({ HistoryTable.messageUuid eq messageUuid }) {
            it[this.deletedBy] = deletedBy
            it[this.deletionReason] = deletionReason
            it[this.deletedAt] = deletedAt
        }
    }

    companion object {
        fun createByRow(row: ResultRow) = HistoryEntry(
            messageUuid = row[HistoryTable.messageUuid],
            senderUuid = row[HistoryTable.senderUuid],
            receiverUuid = row[HistoryTable.receiverUuid],
            messageType = row[HistoryTable.type],
            message = row[HistoryTable.message],
            sentAt = row[HistoryTable.sentAt],
            server = row[HistoryTable.server],
            deleted = row[HistoryTable.deletedAt] != null,
            deletedAt = row[HistoryTable.deletedAt],
            deletedBy = row[HistoryTable.deletedBy],
            deletionReason = row[HistoryTable.deletionReason]
        )
    }
}