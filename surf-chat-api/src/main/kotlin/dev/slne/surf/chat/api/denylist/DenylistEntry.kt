package dev.slne.surf.chat.api.denylist

/**
 * Represents an entry in the denylist system.
 *
 * This interface defines the structure and details associated with a denylist entry.
 * It includes information about the word triggering the denylist, the reason for
 * its presence in the denylist, and metadata about who added it and when it was added.
 * The associated action specifies the type of moderation enforcement to apply when
 * the denylist entry is triggered.
 */
data class DenylistEntry(
    val word: String,
    val reason: String,
    val addedBy: String,
    val addedAt: Long,
    val action: DenylistAction
)