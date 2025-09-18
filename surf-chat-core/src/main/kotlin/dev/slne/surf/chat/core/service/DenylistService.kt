package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.api.entry.DenylistEntry
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectList

/**
 * Represents a service for managing and interacting with a denylist system.
 * The denylist system disallows specific words or phrases, associating them
 * with moderation actions and optional metadata, such as the reason for their inclusion
 * and the details of the user who added the entry.
 * The service provides capabilities to add and remove entries, query entries,
 * and retrieve all denylist entries. It supports both locally-stored entries
 * and entries managed in persistent storage.
 */
interface DenylistService : DatabaseTableHolder {
    /**
     * Adds a new entry to the denylist with the specified details.
     *
     * @param word The word or phrase being added to the denylist.
     * @param reason A description or justification for why the word is being added to the denylist.
     * @param addedBy The identifier of the user who is adding this entry.
     * @param addedAt The timestamp (in milliseconds) when the entry is added.
     * @param action The action to enforce for entries matching this word.
     */
    suspend fun addEntry(
        word: String,
        reason: String,
        addedBy: String,
        addedAt: Long,
        action: DenylistAction
    )

    /**
     * Adds a new local denylist entry with the specified parameters.
     *
     * @param word The word to be added to the local denylist.
     * @param reason The reason for adding the word to the denylist.
     * @param addedBy The identifier of the user who added the entry.
     * @param addedAt The timestamp (in milliseconds) when the entry was added.
     * @param action The denylist action to be applied, specifying the type and duration of the action.
     */
    fun addLocalEntry(
        word: String,
        reason: String,
        addedBy: String,
        addedAt: Long,
        action: DenylistAction
    )

    /**
     * Removes a locally stored denylist entry corresponding to the specified word.
     *
     * @param word The word for which the local denylist entry should be removed.
     */
    fun removeLocalEntry(word: String)

    /**
     * Checks whether a local denylist entry exists for the specified word.
     *
     * @param word The word to check for in the local denylist entries.
     * @return True if a local denylist entry exists for the given word, otherwise false.
     */
    fun hasLocalEntry(word: String): Boolean

    /**
     * Retrieves a local denylist entry for the specified word.
     *
     * This method searches for a denylist entry in the local cache or list
     * for the given word and returns it if found.
     *
     * @param word The word to search for in the local denylist entries.
     * @return The denylist entry associated with the specified word, or null if no entry exists.
     */
    fun getLocalEntry(word: String): DenylistEntry?

    /**
     * Clears all locally stored denylist entries.
     *
     * This method removes all in-memory denylist entries, resetting the local state.
     * It does not affect entries stored in persistent storage or retrieved from external sources.
     */
    fun clearLocalEntries()

    /**
     * Retrieves a list of locally-stored denylist entries.
     *
     * This function returns all denylist entries that have been added locally
     * and are currently active within the system. These entries are not sourced
     * from an external database or synchronized with remote storage but exist
     * only within the local context. Each denylist entry contains information
     * such as the word being denied, the reason for its denial, the user who added
     * it, the timestamp of its addition, and the associated moderation action.
     *
     * @return an ObjectList containing DenylistEntry objects representing the locally-stored denylist entries
     */
    fun getLocalEntries(): ObjectList<DenylistEntry>

    /**
     * Removes a denylist entry corresponding to the given word from the system.
     *
     * @param word The word for which the denylist entry should be removed.
     */
    suspend fun removeEntry(word: String)

    /**
     * Checks whether a denylist entry exists for the specified word.
     *
     * @param word The word to check for in the denylist entries.
     * @return True if a denylist entry exists for the given word, otherwise false.
     */
    suspend fun hasEntry(word: String): Boolean

    /**
     * Retrieves a denylist entry for the specified word.
     *
     * This method fetches the details of a denylist entry corresponding to the provided word.
     * If no matching entry exists, it returns null.
     *
     * @param word The word to search for in the denylist.
     * @return The denylist entry associated with the given word, or null if no match is found.
     */
    suspend fun getEntry(word: String): DenylistEntry?

    /**
     * Retrieves the list of all denylist entries from the denylist system.
     *
     * This method asynchronously fetches all entries in the denylist, each represented
     * by a `DenylistEntry` object containing details about the word, the reason for
     * the entry, the user who added it, when it was added, and the associated action.
     *
     * @return An `ObjectList` containing all denylist entries in the system.
     */
    suspend fun getEntries(): ObjectList<DenylistEntry>

    /**
     * Performs an asynchronous fetch operation for loading or updating data.
     *
     * This method is used to retrieve data or refresh the state of the service,
     * potentially from external sources or by performing necessary calculations
     * and updates. It operates asynchronously and should be invoked within a
     * coroutine scope.
     *
     * The implementation of this method may vary depending on the context in
     * which it is used, such as fetching denylist entries, synchronizing
     * local data with external systems, or initializing the service state.
     *
     * Throws exceptions in case of failure during the fetch operation depending
     * on the implementation specifics, such as network errors or data-related
     * issues.
     */
    suspend fun fetch()

    /**
     * Companion object for the DenylistService class.
     *
     * Provides access to the singleton instance of the DenylistService.
     */
    companion object {
        /**
         * Singleton instance of the DenylistService.
         *
         * Provides access to the service responsible for managing denylist operations, including adding, removing,
         * and querying denylist entries at both local and system-wide levels. The denylist is used
         * to enforce restrictions on specific words or phrases, typically for moderation purposes.
         */
        val INSTANCE = requiredService<DenylistService>()
    }
}

/**
 * A property providing access to the singleton instance of DenylistService.
 * DenylistService is responsible for managing the denylist functionality,
 * which typically includes blocking or restricting specific users or entities
 * from accessing certain features or functionalities within the system.
 */
val denylistService get() = DenylistService.INSTANCE