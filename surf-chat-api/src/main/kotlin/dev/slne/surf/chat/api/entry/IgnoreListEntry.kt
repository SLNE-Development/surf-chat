package dev.slne.surf.chat.api.entry

import java.util.*

/**
 * Represents an entry in the ignore list system.
 *
 * This interface defines the structure and metadata for an ignore list entry,
 * which is used to manage user-based ignore actions in the chat system.
 * It provides properties to identify the user who is ignoring someone,
 * the ignored user's details, and the timestamp of when the ignore action was created.
 */
interface IgnoreListEntry {
    /**
     * Represents the unique identifier of a user in the ignore list entry.
     *
     * This property corresponds to the user initiating the ignore action in the chat system.
     * It uniquely identifies the individual who has decided to ignore another user and
     * is essential for tracking and managing ignore list operations.
     */
    val user: UUID

    /**
     * Represents the name associated with the ignore list entry.
     *
     * This property is typically used to store the display name or username
     * of the individual who is involved in the ignore list operation, whether
     * as the initiator or target of the action.
     */
    val name: String

    /**
     * Represents the unique identifier of the target user in the ignore list entry.
     *
     * This property corresponds to the user being ignored in the ignore list system.
     * It uniquely identifies the target for ignore actions and is essential for
     * distinguishing one user's ignore entry from another.
     */
    val target: UUID

    /**
     * Represents the name of the target associated with an ignore list entry.
     *
     * This property holds the display name or identifier of the user being ignored. It is used
     * to provide a human-readable representation of the target user in contexts such as
     * listing ignored users or retrieving ignore list details.
     */
    val targetName: String

    /**
     * Represents the timestamp (in milliseconds since the epoch) when the entry was created.
     *
     * This property is utilized to record the creation time of an entry, enabling
     * tracking, sorting, or filtering based on when the entry was established.
     * It is particularly useful in systems where the chronological order
     * or historical context of entries is relevant.
     */
    val createdAt: Long
}