package dev.slne.surf.chat.api.entry

import dev.slne.surf.chat.api.DenylistAction

/**
 * Represents an entry in the denylist system.
 *
 * This interface defines the structure and details associated with a denylist entry.
 * It includes information about the word triggering the denylist, the reason for
 * its presence in the denylist, and metadata about who added it and when it was added.
 * The associated action specifies the type of moderation enforcement to apply when
 * the denylist entry is triggered.
 */
interface DenylistEntry {
    /**
     * Represents a word that is subject to denylist rules.
     *
     * This property identifies a specific word that is prohibited or restricted for use
     * within the system. It can be part of a denylist mechanism that enforces content
     * moderation or communication guidelines.
     */
    val word: String

    /**
     * Represents the reason associated with a denylist entry.
     *
     * This property provides a description or justification for why
     * the word or action is included in the denylist. It serves as a
     * contextual explanation for moderators or users interacting
     * with the denylist system.
     */
    val reason: String

    /**
     * Represents the identifier or username of the user who added the denylist entry.
     *
     * This property is used to track the originator responsible for adding a specific
     * word or phrase to the denylist. It can aid in auditing or reviewing denylist changes.
     */
    val addedBy: String

    /**
     * Represents the timestamp (in milliseconds since the epoch) when the entry was added.
     *
     * This property is used to record the time at which a denylist entry was created, allowing
     * for tracking and display of the entry's addition date. It is typically utilized in
     * moderation and audit scenarios within the chat management system.
     */
    val addedAt: Long

    /**
     * Defines the action to be applied when a denylist entry is triggered.
     *
     * The action specifies the type of moderation or enforcement to be taken
     * for a user associated with the denylist, such as banning or muting.
     */
    val action: DenylistAction
}