package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.core.message.MessageData
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface HistoryService : ServiceUsingDatabase {
    suspend fun logMessage(messageData: MessageData)
    suspend fun findHistoryEntry(filter: HistoryFilter): ObjectSet<HistoryEntry>
    suspend fun isLookupRunning(): Boolean

    suspend fun markDeleted(
        messageUuid: UUID,
        deleter: String
    )

    companion object {
        val INSTANCE = requiredService<HistoryService>()
    }
}

val historyService get() = HistoryService.INSTANCE