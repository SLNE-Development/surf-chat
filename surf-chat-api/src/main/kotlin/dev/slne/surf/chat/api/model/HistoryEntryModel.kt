package dev.slne.surf.chat.api.model

import java.util.*

/**
 * Represents a history entry model.
 * This interface defines the structure for storing information about
 * a specific entry in the chat history, including metadata such as
 * the message, timestamp, and deletion details.
 */
interface HistoryEntryModel {

    /**
     * The unique identifier (UUID) of the history entry.
     */
    val entryUuid: UUID

    /**
     * The unique identifier (UUID) of the user associated with the history entry.
     */
    val userUuid: UUID

    /**
     * The type of the history entry (e.g., message type or category).
     */
    val type: String

    /**
     * The timestamp (in milliseconds since epoch) when the history entry was created.
     */
    val timestamp: Long

    /**
     * The content of the message associated with the history entry.
     */
    val message: String

    /**
     * Indicates whether the history entry has been deleted.
     */
    val deleted: Boolean

    /**
     * The identifier (e.g., username or UUID) of the person who deleted the history entry.
     */
    val deletedBy: String

    /**
     * The name of the server where the history entry was created.
     */
    val server: String
}