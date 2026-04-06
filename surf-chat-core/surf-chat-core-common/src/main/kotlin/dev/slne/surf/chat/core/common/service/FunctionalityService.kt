package dev.slne.surf.chat.core.common.service

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.chat.api.functionality.Functionalities

private val service = requiredService<FunctionalityService>()

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
    companion object : FunctionalityService by service
}