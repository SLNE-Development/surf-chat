package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

/**
 * Defines the operations for managing user information, allowing retrieval of online
 * and offline users by unique identifiers or names.
 */
interface UserService {
    /**
     * Retrieves a user by their unique identifier (UUID).
     *
     * @param uuid The unique identifier of the user to be retrieved.
     * @return The user corresponding to the given UUID, or `null` if no such user exists.
     */
    fun getUser(uuid: UUID): User?

    /**
     * Retrieves a user by their name.
     *
     * @param name The name of the user to be retrieved.
     * @return The user instance if a user with the specified name exists, otherwise `null`.
     */
    fun getUser(name: String): User?

    /**
     * Retrieves a user in an offline context either by their UUID or name.
     * This method ensures that a `User` object is returned, even if the user is not currently online.
     *
     * @param uuid The unique identifier of the user to retrieve.
     * @param name The name of the user to retrieve.
     * @return The `User` object associated with the given UUID or name.
     */
    fun getOfflineUser(uuid: UUID, name: String): User

    /**
     * Companion object for accessing the singleton instance of the UserService.
     * UserService provides functionality for retrieving user information,
     * such as obtaining user details by UUID or name, and managing user-related operations.
     */
    companion object {
        /**
         * Singleton instance of the UserService interface.
         *
         * UserService provides methods for retrieving and managing user information
         * such as fetching users by UUID or name and handling offline user data.
         * This instance can be accessed to interact with the implementation of UserService.
         */
        val INSTANCE = requiredService<UserService>()
    }
}

/**
 * A property to access the singleton instance of the UserService.
 * UserService is responsible for managing user-related functionalities
 * within the system, providing access to operations or data pertinent
 * to user management and behavior.
 */
val userService get() = UserService.INSTANCE