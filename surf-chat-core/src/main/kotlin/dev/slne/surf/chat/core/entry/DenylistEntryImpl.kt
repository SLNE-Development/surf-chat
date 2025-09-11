package dev.slne.surf.chat.core.entry

import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.api.entry.DenylistEntry

/**
 * Represents the implementation of a denylist entry in the chat moderation system.
 *
 * This data class provides details about a specific word or phrase included in the denylist,
 * including the associated moderation action, metadata about its addition, and a justification for its inclusion.
 * It is an implementation of the `DenylistEntry` interface.
 *
 * @property word The word or phrase that triggers the denylist entry.
 * @property reason A description or justification for why the word is included in the denylist.
 * @property addedBy The identifier of the user who added this entry to the denylist.
 * @property addedAt The timestamp of when the entry was added, represented as milliseconds since the epoch.
 * @property action The moderation action to be executed when the denylist entry is triggered.
 */
data class DenylistEntryImpl(
    override val word: String,
    override val reason: String,
    override val addedBy: String,
    override val addedAt: Long,
    override val action: DenylistAction
) : DenylistEntry