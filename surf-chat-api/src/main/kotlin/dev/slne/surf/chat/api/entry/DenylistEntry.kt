package dev.slne.surf.chat.api.entry

import dev.slne.surf.chat.api.DenylistAction

/**
 * Represents an entry in the denylist.
 *
 * @property word The word that is denied.
 * @property reason The reason why the word is denied.
 * @property addedBy The name of the user who added the word.
 * @property addedAt The timestamp (in milliseconds since epoch) when the word was added.
 */
interface DenylistEntry {
    val word: String
    val reason: String
    val addedBy: String
    val addedAt: Long
    val action: DenylistAction
}