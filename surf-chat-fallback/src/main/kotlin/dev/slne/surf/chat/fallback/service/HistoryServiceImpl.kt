package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.service.HistoryService
import dev.slne.surf.chat.fallback.table.HistoryTable
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.*
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.upsert
import dev.slne.surf.surfapi.core.api.messages.adventure.plain
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(HistoryService::class)
class HistoryServiceImpl : HistoryService, Services.Fallback {
    private val loadHistoryMutex = Mutex()

    override suspend fun logMessage(messageData: MessageData) =
        suspendTransaction {
            HistoryTable.insert {
                it[messageUuid] = messageData.messageUuid
                it[senderUuid] = messageData.sender.uuid
                it[receiverUuid] = messageData.receiver?.uuid
                it[message] = messageData.message.plain()
                it[sentAt] = messageData.sentAt
                it[server] = messageData.server
                it[type] = messageData.type
                it[deletedBy] = null
            }
            return@suspendTransaction
        }

    override suspend fun findHistoryEntry(filter: HistoryFilter): ObjectSet<HistoryEntry> =
        withTimeout(10_000L) {
            loadHistoryMutex.withLock {
                suspendTransaction {
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
                        HistoryEntry(
                            messageUuid = it[HistoryTable.messageUuid],
                            senderUuid = it[HistoryTable.senderUuid],
                            messageType = it[HistoryTable.type],
                            sentAt = it[HistoryTable.sentAt],
                            message = it[HistoryTable.message],
                            server = it[HistoryTable.server],
                            deletedBy = it[HistoryTable.deletedBy],
                            receiverUuid = it[HistoryTable.receiverUuid]
                        )
                    }.toSet().toObjectSet()
                }
            }
        }


    override suspend fun isLookupRunning(): Boolean {
        return loadHistoryMutex.isLocked
    }

    override suspend fun markDeleted(messageUuid: UUID, deleter: String) =
        suspendTransaction {
            HistoryTable.upsert(where = ({ HistoryTable.messageUuid eq messageUuid })) {
                it[deletedBy] = deleter
            }
            return@suspendTransaction
        }
}