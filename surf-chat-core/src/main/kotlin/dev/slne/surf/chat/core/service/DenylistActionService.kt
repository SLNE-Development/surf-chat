package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.entry.DenylistEntry
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.chat.SignedMessage
import java.util.*

/**
 * Represents a service interface for managing denylist-related actions.
 *
 * This interface provides functionality for adding, removing, and interacting with
 * actions that are part of the denylist system. It supports both local and remote
 * operations and enables interaction with denylist entries to enforce system-wide rules.
 */
interface DenylistActionService {
    /**
     * Adds a specified denylist action to the system.
     *
     * This coroutine function is used to register a new action in the denylist.
     * It processes actions represented by the `DenylistAction` interface and integrates
     * them into the broader denylist system. Implementation details may vary depending
     * on the underlying data storage or processing logic.
     *
     * @param action The denylist action to be added. This parameter must implement
     *               the `DenylistAction` interface and provide details such as
     *               the unique name, action type, reason, and duration.
     */
    suspend fun addAction(action: DenylistAction)

    /**
     * Removes a specified denylist action from the system.
     *
     * @param action The denylist action to be removed. This action includes details such as the name,
     *               type of action, reason, and duration.
     */
    suspend fun removeAction(action: DenylistAction)

    /**
     * Checks if an action with the specified name exists within the denylist.
     *
     * @param name The unique name of the action to check for existence.
     * @return True if the action exists in the denylist, false otherwise.
     */
    suspend fun hasAction(name: String): Boolean

    /**
     * Fetches a collection of actions related to the denylist.
     *
     * This method is a coroutine function and is expected to be executed within a coroutine scope.
     * It retrieves the current list of denylist actions, potentially from a remote source or a database,
     * depending on the implementation of the `DenylistActionService` interface.
     *
     * This method is primarily used to synchronize or update the locally available denylist actions
     * with the current state of the system.
     *
     * It does not take any parameters and does not return a value directly. Additional behaviors
     * or processing depend on the implementation of the method.
     *
     * Throws exceptions if there are issues fetching the actions (e.g., connectivity issues or other errors).
     */
    suspend fun fetchActions()

    /**
     * Adds a denylist action to the local list of actions.
     * This method does not persist the action to a database or external storage.
     *
     * @param action The denylist action to be added. It includes details such as the name,
     *               type of action, reason, and duration.
     * @return True if the action was successfully added to the local list, false otherwise.
     */
    fun addLocalAction(action: DenylistAction): Boolean

    /**
     * Removes a denylist action from the local list of actions.
     *
     * @param action The denylist action to be removed. This action includes details like
     *               name, type, reason, and duration, identifying the specific action.
     * @return True if the action was successfully removed from the local list, false otherwise.
     */
    fun removeLocalAction(action: DenylistAction): Boolean

    /**
     * Retrieves a locally stored denylist action by its unique name.
     *
     * @param name The unique name of the denylist action to be retrieved.
     * @return The denylist action corresponding to the provided name, or null if no such action exists.
     */
    fun getLocalAction(name: String): DenylistAction?

    /**
     * Lists all local denylist actions currently registered.
     *
     * @return a set of denylist actions available in the local context, represented as an ObjectSet of DenylistAction.
     */
    fun listLocalActions(): ObjectSet<DenylistAction>

    /**
     * Checks if a local denylist action exists for the specified name.
     *
     * @param name The unique name of the denylist action to check.
     * @return True if a local action with the specified name exists, false otherwise.
     */
    fun hasLocalAction(name: String): Boolean


    /**
     * Processes an action based on the provided denylist entry and message details.
     *
     * This coroutine function performs the necessary operations related to a denylist entry,
     * such as applying moderation actions or logging the event, based on the context of
     * the message and its sender.
     *
     * @param messageUuid The unique identifier of the message being processed.
     * @param entry The denylist entry that is triggered and associated with the action.
     * @param message The signed message containing details of the content, sender, and any cryptographic signature.
     * @param sender The user who sent the message that triggered the denylist action.
     */
    suspend fun makeAction(
        messageUuid: UUID,
        entry: DenylistEntry,
        message: SignedMessage,
        sender: User
    )

    /**
     * Companion object for the `DenylistActionService` class.
     *
     * Provides a single instance of the `DenylistActionService`, accessible globally.
     * The instance is obtained using the `requiredService` utility, which ensures
     * proper initialization and retrieval of the service.
     */
    companion object {
        /**
         * Singleton instance of the `DenylistActionService`, providing access to functionality
         * for managing denylist actions within the system.
         *
         * This instance is used to perform operations such as adding, removing, fetching,
         * and checking the existence of denylist actions. It acts as the centralized point
         * for interacting with denylist-related features and services, ensuring consistent
         * handling and integration across the application.
         */
        val INSTANCE = requiredService<DenylistActionService>()
    }
}

/**
 *
 */
val denylistActionService get() = DenylistActionService.INSTANCE