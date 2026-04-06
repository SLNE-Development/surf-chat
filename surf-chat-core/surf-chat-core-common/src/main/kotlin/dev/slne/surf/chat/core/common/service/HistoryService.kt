package dev.slne.surf.chat.core.common.service

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.api.message.MessageData
import it.unimi.dsi.fastutil.objects.ObjectList
import java.time.OffsetDateTime
import java.util.*

private val service = requiredService<HistoryService>()

interface HistoryService {
    /**
     * Logs the provided message data into the system's history.
     *
     * @param messageData The data of the message to be logged, including content, sender,
     * receiver, timestamps, and additional metadata describing the message context.
     */
    suspend fun logMessage(messageData: MessageData)

    /**
     * Finds chat history entries that match the provided filter criteria.
     *
     * @param filter The filter criteria to use for querying the chat history.
     * @return An ObjectSet containing the history entries that match the filter.
     */
    suspend fun findHistoryEntry(filter: HistoryFilter): ObjectList<HistoryEntry>

    /**
     * Retrieves the count of available lookup attempts or operations allowed.
     *
     * This method is intended to provide a numerical representation of how many
     * lookup actions can currently be performed without exceeding any configured limitations.
     *
     * @return The number of lookup operations currently available.
     */
    suspend fun availableLookups(): Int

    /**
     * Marks a message as deleted in the chat history.
     *
     * @param messageUuid The unique identifier of the message to be marked as deleted.
     * @param deleter The identifier of the user or system performing the delete operation.
     */
    suspend fun markDeleted(
        messageUuid: UUID,
        deletedBy: UUID?,
        deletionReason: String? = null,
        deletedAt: OffsetDateTime = OffsetDateTime.now()
    )

    /**
     * Companion object for the `HistoryService` interface.
     * Provides access to the singleton instance of `HistoryService`.
     */
    companion object : HistoryService by service
}