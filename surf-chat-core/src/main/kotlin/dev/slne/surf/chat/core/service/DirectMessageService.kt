package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

/**
 * Service interface responsible for managing the enablement and disablement of direct messages for users.
 * This service allows querying if direct messages are enabled, and provides the functionality to enable or disable them.
 */
interface DirectMessageService : DatabaseTableHolder {
    /**
     * Checks if direct messages are enabled for the specified user.
     *
     * @param uuid The unique identifier of the user to check.
     * @return `true` if direct messages are enabled for the user, otherwise `false`.
     */
    suspend fun directMessagesEnabled(uuid: UUID): Boolean

    /**
     * Enables direct messages for a user specified by their UUID.
     *
     * @param uuid The unique identifier of the user for whom direct messages will be enabled.
     */
    suspend fun enableDirectMessages(uuid: UUID)

    /**
     * Disables direct messages for the user associated with the given UUID.
     *
     * @param uuid The unique identifier of the user for whom direct messages will be disabled.
     */
    suspend fun disableDirectMessages(uuid: UUID)

    /**
     * Companion object providing access to the singleton instance of the `DirectMessageService`.
     * This singleton instance can be used to perform operations related to direct messaging services.
     */
    companion object {
        /**
         * Singleton instance of the DirectMessageService.
         * The DirectMessageService provides functionality to manage and control direct messaging capabilities,
         * including enabling and disabling direct message features for specific users.
         */
        val INSTANCE = requiredService<DirectMessageService>()
    }
}

/**
 * A property to access the singleton instance of the DirectMessageService.
 * DirectMessageService facilitates handling direct private messages between users within the system.
 */
val directMessageService get() = DirectMessageService.INSTANCE