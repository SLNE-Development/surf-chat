package dev.slne.surf.chat.core.client.service

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.messages.adventure.plain
import dev.slne.surf.api.core.util.toObjectList
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.client.rabbitApi
import dev.slne.surf.chat.core.common.rabbit.packet.request.history.CreateHistoryEntryRequestPacket
import dev.slne.surf.chat.core.common.rabbit.packet.request.history.FindHistoriesRequestPacket
import dev.slne.surf.chat.core.common.rabbit.packet.request.history.MarkHistoryEntryDeletedRequestPacket
import dev.slne.surf.chat.core.common.service.HistoryService
import it.unimi.dsi.fastutil.objects.ObjectList
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
        rabbitApi.sendRequest(
            CreateHistoryEntryRequestPacket(
                HistoryEntry(
                    messageUuid = messageData.messageUuid,
                    senderUuid = messageData.sender,
                    receiverUuid = messageData.receiver,
                    messageType = messageData.type,
                    sentAt = messageData.sentAt,
                    message = messageData.message.plain(),
                    server = messageData.server,
                    deleted = false
                )
            )
        )
    }

    override suspend fun findHistoryEntry(
        filter: HistoryFilter
    ): ObjectList<HistoryEntry> = withTimeout(10.seconds) {
        loadHistorySemaphore.withPermit {
            rabbitApi.sendRequest(FindHistoriesRequestPacket(filter)).histories.toObjectList()
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
        rabbitApi.sendRequest(MarkHistoryEntryDeletedRequestPacket(messageUuid, deletedBy, deletionReason, deletedAt))
    }
}
