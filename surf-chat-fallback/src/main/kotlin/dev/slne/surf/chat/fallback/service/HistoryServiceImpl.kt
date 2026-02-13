package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.service.HistoryService
import dev.slne.surf.chat.fallback.repository.history.HistoryRepository
import dev.slne.surf.surfapi.core.api.messages.adventure.plain
import dev.slne.surf.surfapi.core.api.util.toObjectList
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectList
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withTimeout
import net.kyori.adventure.util.Services
import java.time.OffsetDateTime
import java.util.*
import kotlin.time.Duration.Companion.seconds

@AutoService(HistoryService::class)
class HistoryServiceImpl : HistoryService, Services.Fallback {
    private val loadHistorySemaphore = Semaphore(16)

    override suspend fun logMessage(messageData: MessageData) {
        HistoryRepository.createHistoryEntry(
            messageUuid = messageData.messageUuid,
            senderUuid = messageData.sender,
            receiverUuid = messageData.receiver,
            message = messageData.message.plain(),
            sentAt = messageData.sentAt,
            type = messageData.type,
            server = messageData.server
        )
    }

    override suspend fun findHistoryEntry(
        filter: HistoryFilter
    ): ObjectList<HistoryEntry> = withTimeout(10.seconds) {
        loadHistorySemaphore.withPermit {
            HistoryRepository.findHistories(filter).toObjectList()
        }
    }

    override suspend fun availableLookups(): Int {
        return loadHistorySemaphore.availablePermits
    }

    override suspend fun markDeleted(
        messageUuid: UUID,
        deletedBy: UUID?,
        deletionReason: String?,
        deletedAt: OffsetDateTime
    ) {
        HistoryRepository.markDeleted(messageUuid, deletedBy, deletionReason, deletedAt)
    }
}