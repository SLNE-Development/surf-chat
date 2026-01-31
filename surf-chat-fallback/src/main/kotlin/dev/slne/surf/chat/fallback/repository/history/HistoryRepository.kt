package dev.slne.surf.chat.fallback.repository.history

import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageType
import java.time.OffsetDateTime
import java.util.*

interface HistoryRepository {
    suspend fun createHistoryEntry(
        messageUuid: UUID,
        senderUuid: UUID,
        receiverUuid: UUID?,
        message: String,
        sentAt: OffsetDateTime,
        type: MessageType,
        server: String
    )

    suspend fun markDeleted(
        messageUuid: UUID,
        deletedBy: UUID,
        deletionReason: String?,
        deletedAt: OffsetDateTime
    )

    suspend fun findHistories(filter: HistoryFilter): Set<HistoryEntry>

    companion object : HistoryRepository by HistoryRepositoryImpl()
}