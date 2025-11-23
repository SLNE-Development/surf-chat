package dev.slne.surf.chat.server.database.repository

import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.core.common.message.MessageData
import dev.slne.surf.chat.core.common.netty.packet.serializer.ChatUuid
import dev.slne.surf.chat.server.database.entity.HistoryEntity
import dev.slne.surf.chat.server.database.table.HistoryTable
import dev.slne.surf.cloud.api.server.plugin.CoroutineTransactional
import dev.slne.surf.surfapi.core.api.messages.adventure.plain
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.springframework.stereotype.Repository

@Repository
@CoroutineTransactional
class HistoryRepository {
    private val loadHistoryMutex = Mutex()

    suspend fun logMessage(messageData: MessageData) = HistoryEntity.new {
        messageUuid = messageData.messageUuid
        senderUuid = messageData.sender.uuid
        receiverUuid = messageData.receiver?.uuid
        message = messageData.message.plain()
        sentAt = messageData.sentAt
        server = messageData.server
        channel = messageData.channel
        type = messageData.type
        deletedBy = null
    }

    suspend fun findHistoryEntry(filter: HistoryFilter?): ObjectSet<HistoryEntry> =
        withTimeout(30_000L) {
            loadHistoryMutex.withLock {
                val now = System.currentTimeMillis()
                val conditions = mutableListOf<Op<Boolean>>()

                filter?.senderUuid?.let {
                    conditions += HistoryTable.senderUuid eq it
                }

                filter?.receiverUuid?.let {
                    conditions += HistoryTable.receiverUuid eq it
                }

                filter?.messageType?.let {
                    conditions += HistoryTable.type eq it
                }

                filter?.type?.let {
                    conditions += HistoryTable.type eq it
                }

                filter?.range?.let {
                    val minTime = now - it
                    conditions += HistoryTable.sentAt greaterEq minTime
                }

                filter?.messageLike?.let {
                    conditions += HistoryTable.message like "%$it%"
                }

                filter?.deletedBy?.let {
                    conditions += HistoryTable.deletedBy eq it
                }

                filter?.server?.let {
                    conditions += HistoryTable.server eq it
                }

                filter?.channel?.let {
                    conditions += HistoryTable.channel eq it
                }

                filter?.messageUuid?.let {
                    conditions += HistoryTable.messageUuid eq it
                }

                val query = if (conditions.isNotEmpty()) {
                    HistoryTable.selectAll()
                        .where(conditions.reduce { acc, cond -> acc and cond })
                } else {
                    HistoryTable.selectAll()
                }

                val limitedQuery = filter?.limit?.let { query.limit(it) } ?: query

                limitedQuery.map {
                    HistoryEntry(
                        messageUuid = it[HistoryTable.messageUuid],
                        senderUuid = it[HistoryTable.senderUuid],
                        messageType = it[HistoryTable.type],
                        sentAt = it[HistoryTable.sentAt],
                        message = it[HistoryTable.message],
                        server = it[HistoryTable.server],
                        deletedBy = it[HistoryTable.deletedBy],
                        receiverUuid = it[HistoryTable.receiverUuid],
                        channel = it[HistoryTable.channel]
                    )
                }.toObjectSet()
            }
        }


    fun isLookupRunning() = loadHistoryMutex.isLocked

    suspend fun markDeleted(messageUuid: ChatUuid, deleter: String) =
        HistoryEntity.findSingleByAndUpdate(HistoryTable.messageUuid eq messageUuid) {
            it.deletedBy = deleter
        }
}