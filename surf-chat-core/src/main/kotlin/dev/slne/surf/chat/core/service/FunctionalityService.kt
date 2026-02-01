package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.core.functionality.Functionalities
import dev.slne.surf.surfapi.core.api.util.requiredService

/**
 * Provides functionality for managing server-specific features and their states.
 * This service allows enabling or disabling features for specific servers,
 * fetching server states, and managing local server configurations.
 */
interface FunctionalityService {
    /**
     * Fetches data or performs operations for the specified chat server.
     *
     * @param localServer The chat server for which the fetch operation is to be performed.
     *                    This server instance provides the necessary context and configuration
     *                    for the operation.
     */
    suspend fun fetch(localServer: String)

    fun getFunctionalities(): Functionalities
    suspend fun updateLocalFunctionalities(functionalities: Functionalities)
    suspend fun updateLocalFunctionalities(update: (Functionalities) -> Functionalities) {
        updateLocalFunctionalities(update(getFunctionalities()))
    }

    suspend fun updateFunctionalities(functionalities: Functionalities, localServer: String)
    suspend fun updateFunctionalities(localServer: String, update: (Functionalities) -> Functionalities) {
        updateFunctionalities(update(getFunctionalities(localServer)), localServer)
    }

    suspend fun getFunctionalities(localServer: String): Functionalities
    suspend fun getFunctionalitiesForAllServers(): Map<String, Functionalities>

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