package dev.slne.surf.chat.api.model

/**
 * Represents an entry in the blacklist for chat messages.
 * This interface defines the structure for storing information about
 * blacklisted words, including the word itself, the reason for blacklisting,
 * and metadata about when and by whom it was added.
 */
interface BlacklistWordEntry {

    /**
     * The blacklisted word.
     */
    val word: String

    /**
     * The reason why the word was blacklisted.
     */
    val reason: String

    /**
     * The timestamp (in milliseconds since epoch) when the word was added to the blacklist.
     */
    val addedAt: Long

    /**
     * The identifier (e.g., username or UUID) of the person who added the word to the blacklist.
     */
    val addedBy: String
}