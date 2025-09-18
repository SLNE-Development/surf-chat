package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.entry.HistoryEntry
import dev.slne.surf.chat.api.entry.HistoryFilter
import dev.slne.surf.chat.core.message.MessageData
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

/**
 * Represents a service for managing chat history, including logging, retrieval, and deletion
 * of messages. This interface defines methods to interact with the history of messages within
 * the chat system, including querying based on various filters, monitoring log status, and
 * marking messages as deleted.
 */
interface HistoryService : DatabaseTableHolder {
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
    suspend fun findHistoryEntry(filter: HistoryFilter): ObjectSet<HistoryEntry>

    /**
     * Checks if a lookup operation is currently running in the system.
     *
     * @return `true` if a lookup operation is in progress, `false` otherwise.
     */
    suspend fun isLookupRunning(): Boolean

    /**
     * Marks a message as deleted in the chat history.
     *
     * @param messageUuid The unique identifier of the message to be marked as deleted.
     * @param deleter The identifier of the user or system performing the delete operation.
     */
    suspend fun markDeleted(
        messageUuid: UUID,
        deleter: String
    )

    /**
     * Companion object for the `HistoryService` interface.
     * Provides access to the singleton instance of `HistoryService`.
     */
    companion object {
        /**
         * Singleton instance of the HistoryService.
         *
         * This property provides access to the centralized HistoryService instance, which manages
         * operations related to logging messages, querying history entries, checking lookup statuses,
         * and marking messages as deleted in the system.
         */
        val INSTANCE = requiredService<HistoryService>()
    }
}

/**
 * Provides access to the singleton instance of HistoryService.
 * HistoryService is responsible for handling operations and interactions
 * related to maintaining historical data within the system.
 */
val historyService get() = HistoryService.INSTANCE