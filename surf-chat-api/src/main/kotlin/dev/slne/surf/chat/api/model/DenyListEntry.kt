package dev.slne.surf.chat.api.model

/**
 * Represents an entry in the denylist for chat messages.
 * This interface defines the structure for storing information about
 * denylisting words, including the word itself, the reason for denylisting,
 * and metadata about when and by whom it was added.
 */
interface DenyListEntry {

    /**
     * The denylisted word.
     */
    val word: String

    /**
     * The reason why the word was denylisted.
     */
    val reason: String

    /**
     * The timestamp (in milliseconds since epoch) when the word was added to the denylisted.
     */
    val addedAt: Long

    /**
     * The identifier (name) of the person who added the word to the denylist.
     */
    val addedBy: String
}