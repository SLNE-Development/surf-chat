package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.server.ChatServer
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet

/**
 * Provides functionality for managing server-specific features and their states.
 * This service allows enabling or disabling features for specific servers,
 * fetching server states, and managing local server configurations.
 */
interface FunctionalityService : DatabaseTableHolder {
    /**
     * Determines whether the specified chat server has the required functionality enabled.
     *
     * @param server The chat server to check for the enabled state of the functionality.
     * @return `true` if the functionality is enabled for the specified chat server, `false` otherwise.
     */
    suspend fun isEnabledForServer(server: ChatServer): Boolean

    /**
     * Retrieves a set of all chat servers currently registered in the system.
     *
     * @return a set of all available chat servers.
     */
    suspend fun getAllServers(): ObjectSet<ChatServer>

    /**
     * Retrieves a set of all chat servers that are currently enabled.
     *
     * @return an ObjectSet containing all enabled ChatServer instances.
     */
    suspend fun getAllEnabledServers(): ObjectSet<ChatServer>

    /**
     * Retrieves a set of all chat servers that are currently disabled.
     *
     * @return An ObjectSet containing all ChatServer instances classified as disabled.
     */
    suspend fun getAllDisabledServers(): ObjectSet<ChatServer>

    /**
     * Fetches data or performs operations for the specified chat server.
     *
     * @param localServer The chat server for which the fetch operation is to be performed.
     *                    This server instance provides the necessary context and configuration
     *                    for the operation.
     */
    suspend fun fetch(localServer: ChatServer)

    /**
     * Enables the local chat functionality for the specified chat server.
     * This operation allows the server to support local chat features if they are not already enabled.
     *
     * @param localServer The chat server instance for which the local chat will be enabled.
     */
    suspend fun enableLocalChat(localServer: ChatServer)

    /**
     * Toggles the local chat's enabled state for the specified chat server.
     * If the local chat is currently enabled for the given server, it will be disabled.
     * If it is currently disabled, it will be enabled.
     *
     * @param localServer The chat server for which the local chat's state will be toggled.
     *                    Uses the server's internal attributes to identify its current state.
     * @return A boolean value indicating the new enabled state of the local chat for the given server.
     *         Returns true if the local chat is now enabled, otherwise false.
     */
    suspend fun toggleLocalChat(localServer: ChatServer): Boolean

    /**
     * Disables the local chat functionality for the specified chat server.
     * This operation prevents the server from supporting local chat features if they are currently enabled.
     *
     * @param localServer The chat server instance for which the local chat will be disabled.
     */
    suspend fun disableLocalChat(localServer: ChatServer)

    /**
     * Checks if the local chat feature is currently enabled.
     *
     * @return true if local chat is enabled, false otherwise.
     */
    fun isLocalChatEnabled(): Boolean

    /**
     * A companion object that provides access to the singleton instance of the FunctionalityService.
     * This singleton can be used to interact with functionalities related to server configurations,
     * enabling and disabling local chat, and managing the state of chat servers.
     */
    companion object {
        /**
         * Singleton instance of the `FunctionalityService`.
         * The `FunctionalityService` provides functionality related to managing and querying server-level chat capabilities.
         * It includes methods for checking server configurations, enabling or disabling chat functionalities,
         * toggling chat states, and fetching associated data about servers within the system.
         */
        val INSTANCE = requiredService<FunctionalityService>()
    }
}

/**
 *
 */
val functionalityService get() = FunctionalityService.INSTANCE