package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.core.entry.HistoryEntryImpl
import dev.slne.surf.chat.core.message.MessageData
import dev.slne.surf.chat.core.service.HistoryService
import dev.slne.surf.chat.fallback.table.HistoryTable
import dev.slne.surf.surfapi.core.api.messages.adventure.plain
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

@AutoService(HistoryService::class)
class FallbackHistoryService : HistoryService, Services.Fallback {
    private val loadHistoryMutex = Mutex()

    override fun createTable() {
        transaction {
            SchemaUtils.create(HistoryTable)
        }
    }

    override suspend fun logMessage(messageData: MessageData) =
        newSuspendedTransaction(Dispatchers.IO) {
            HistoryTable.insert {
                it[messageUuid] = messageData.messageUuid
                it[senderUuid] = messageData.sender.uuid
                it[receiverUuid] = messageData.receiver?.uuid
                it[message] = messageData.message.plain()
                it[sentAt] = messageData.sentAt
                it[server] = messageData.server
                it[channel] = messageData.channel?.channelName
                it[type] = messageData.type
                it[deletedBy] = null
            }

            return@newSuspendedTransaction
        }

    override suspend fun findHistoryEntry(filter: HistoryFilter): ObjectSet<HistoryEntry> =
        withTimeout(10_000L) {
            loadHistoryMutex.withLock {
                newSuspendedTransaction(Dispatchers.IO) {
                    val now = System.currentTimeMillis()
                    val conditions = mutableListOf<Op<Boolean>>()

                    filter.senderUuid?.let {
                        conditions += HistoryTable.senderUuid eq it
                    }

                    filter.receiverUuid?.let {
                        conditions += HistoryTable.receiverUuid eq it
                    }

                    filter.messageType?.let {
                        conditions += HistoryTable.type eq it
                    }

                    filter.type?.let {
                        conditions += HistoryTable.type eq it
                    }

                    filter.range?.let {
                        val minTime = now - it
                        conditions += HistoryTable.sentAt greaterEq minTime
                    }

                    filter.messageLike?.let {
                        conditions += HistoryTable.message like "%$it%"
                    }

                    filter.deletedBy?.let {
                        conditions += HistoryTable.deletedBy eq it
                    }

                    filter.server?.let {
                        conditions += HistoryTable.server eq it
                    }

                    filter.channel?.let {
                        conditions += HistoryTable.channel eq it
                    }

                    filter.messageUuid?.let {
                        conditions += HistoryTable.messageUuid eq it
                    }

                    val query = if (conditions.isNotEmpty()) {
                        HistoryTable.selectAll()
                            .where(conditions.reduce { acc, cond -> acc and cond })
                    } else {
                        HistoryTable.selectAll()
                    }

                    val limitedQuery = filter.limit?.let { query.limit(it) } ?: query

                    limitedQuery.map {
                        HistoryEntryImpl(
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
        }


    override suspend fun isLookupRunning(): Boolean {
        return loadHistoryMutex.isLocked
    }

    override suspend fun markDeleted(messageUuid: UUID, deleter: String) =
        newSuspendedTransaction(
            Dispatchers.IO
        ) {
            HistoryTable.update({ HistoryTable.messageUuid eq messageUuid }) {
                it[deletedBy] = deleter
            }
            println("[$deleter] deleted message with uuid: $messageUuid")
            return@newSuspendedTransaction
        }
}