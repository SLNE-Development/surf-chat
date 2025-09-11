package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.entry.DenylistEntry
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.chat.SignedMessage
import java.util.*

/**
 * Service interface for managing actions related to a denylist.
 * Provides both local and persistent management of denylist actions.
 */
interface DenylistActionService {
    /**
     * Adds a specified `DenylistAction` to the system.
     *
     * @param action The `DenylistAction` object that needs to be added. This action typically includes
     *               the action name, type, and reason, which define its behavior in the denylist process.
     */
    suspend fun addAction(action: DenylistAction)

    /**
     * Removes a specified `DenylistAction` from the system.
     *
     * @param action The `DenylistAction` object that needs to be removed.
     */
    suspend fun removeAction(action: DenylistAction)

    suspend fun hasAction(name: String): Boolean

    /**
     * Retrieves the list of actions related to the Denylist from the desired source.
     * This method is implemented as a suspending function, meaning it can perform
     * asynchronous operations without blocking the thread.
     *
     * Use this function to fetch the relevant Denylist actions currently available
     * for processing or review.
     *
     * This function interacts with the broader Denylist management system and may involve
     * accessing remote or local resources to retrieve the data.
     *
     * Note: Ensure appropriate handling of the context and coroutine scope when invoking this method.
     */
    suspend fun fetchActions()

    /**
     * Adds a denylist action to the local storage.
     *
     * @param action The denylist action to be added.
     * @return `true` if the action was successfully added, `false` otherwise.
     */
    fun addLocalAction(action: DenylistAction): Boolean

    /**
     * Removes a specified denylist action from the local storage.
     *
     * @param action The denylist action to be removed.
     * @return `true` if the action was successfully removed, `false` otherwise.
     */
    fun removeLocalAction(action: DenylistAction): Boolean

    /**
     * Retrieves a local DenylistAction by its name.
     *
     * @param name the name of the DenylistAction to retrieve
     * @return the DenylistAction associated with the given name, or null if no match is found
     */
    fun getLocalAction(name: String): DenylistAction?

    /**
     * Retrieves a set of all local denylist actions currently stored in the system.
     *
     * @return An ObjectSet containing `DenylistAction` instances that represent
     * the available local denylist actions.
     */
    fun listLocalActions(): ObjectSet<DenylistAction>

    fun hasLocalAction(name: String): Boolean


    suspend fun makeAction(
        messageUuid: UUID,
        entry: DenylistEntry,
        message: SignedMessage,
        sender: User
    )

    /**
     * Companion object for the DenylistActionService interface.
     * Provides a globally accessible instance of the DenylistActionService.
     */
    companion object {
        /**
         * Singleton instance of the DenylistActionService. This service provides mechanisms
         * to manage actions related to denylist entries, including adding, removing,
         * fetching entries, and performing specific denylist-related actions.
         */
        val INSTANCE = requiredService<DenylistActionService>()
    }
}

/**
 * A reference to the singleton instance of the `DenylistService` interface.
 * This service provides functionality for managing denylist entries, including
 * adding, removing, and retrieving entries both locally and externally. The service
 * supports enforcement of denial actions based on specific words or patterns.
 *
 * The `DenylistService` is primarily responsible for maintaining a repository of prohibited
 * words or terms, each associated with a reason, timestamp, user who added it, and an action
 * defining how it should be handled.
 */
val denylistActionService get() = DenylistActionService.INSTANCE