package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.model.MessageType
import dev.slne.surf.chat.core.entry.HistoryEntryImpl
import dev.slne.surf.chat.core.message.MessageData
import dev.slne.surf.chat.core.service.HistoryService
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
import java.util.*

@AutoService(HistoryService::class)
class FallbackHistoryService : HistoryService, Services.Fallback {
    private val loadHistoryMutex = Mutex()

    object HistoryEntries : Table("chat_history") {
        val messageUuid =
            varchar("message_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val senderUuid =
            varchar("sender_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val receiverUuid =
            varchar("receiver_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
                .nullable()
        val message = text("message")
        val sentAt = long("sent_at")
        val type = text("type").transform({ MessageType.valueOf(it) }, { it.name })
        val server = text("server")
        val channel = text("channel_name").nullable()
        val deletedBy = text("deleted_by").nullable()

        override val primaryKey = PrimaryKey(messageUuid)
    }

    override fun createTable() {
        SchemaUtils.create(HistoryEntries)
    }

    override suspend fun logMessage(messageData: MessageData) =
        newSuspendedTransaction(Dispatchers.IO) {
            HistoryEntries.insert {
                it[messageUuid] = messageData.messageUuid
                it[senderUuid] = messageData.sender.uuid
                it[receiverUuid] = messageData.receiver?.uuid
                it[message] = messageData.message.plain()
                it[sentAt] = messageData.sentAt
                it[server] = messageData.server
                it[channel] = messageData.channel?.channelName
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
                        conditions += HistoryEntries.senderUuid eq it
                    }

                    filter.messageType?.let {
                        conditions += HistoryEntries.type eq it
                    }

                    filter.range?.let {
                        val minTime = now - it
                        conditions += HistoryEntries.sentAt greaterEq minTime
                    }

                    filter.messageLike?.let {
                        conditions += HistoryEntries.message like "%$it%"
                    }

                    filter.deletedBy?.let {
                        conditions += HistoryEntries.deletedBy eq it
                    }

                    filter.server?.let {
                        conditions += HistoryEntries.server eq it
                    }

                    filter.messageUuid?.let {
                        conditions += HistoryEntries.messageUuid eq it
                    }

                    val query = if (conditions.isNotEmpty()) {
                        HistoryEntries.select(conditions.reduce { acc, cond -> acc and cond })
                    } else {
                        HistoryEntries.selectAll()
                    }

                    query.map {
                        HistoryEntryImpl(
                            messageUuid = it[HistoryEntries.messageUuid],
                            senderUuid = it[HistoryEntries.senderUuid],
                            messageType = it[HistoryEntries.type],
                            sentAt = it[HistoryEntries.sentAt],
                            messageLike = it[HistoryEntries.message],
                            server = it[HistoryEntries.server],
                            deletedBy = it[HistoryEntries.deletedBy]
                        )
                    }.toObjectSet()
                }
            }
        }

    override suspend fun isLookupRunning(): Boolean {
        return loadHistoryMutex.isLocked
    }

}