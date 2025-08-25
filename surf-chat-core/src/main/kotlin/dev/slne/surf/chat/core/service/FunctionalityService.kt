package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.server.ChatServer
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet

interface FunctionalityService : ServiceUsingDatabase {
    suspend fun isEnabledForServer(server: ChatServer): Boolean

    suspend fun getAllServers(): ObjectSet<ChatServer>
    suspend fun getAllEnabledServers(): ObjectSet<ChatServer>
    suspend fun getAllDisabledServers(): ObjectSet<ChatServer>

    suspend fun enableLocalChat(localServer: ChatServer)
    suspend fun toggleLocalChat(localServer: ChatServer): Boolean
    suspend fun disableLocalChat(localServer: ChatServer)
    fun isLocalChatEnabled(): Boolean

    companion object {
        val INSTANCE = requiredService<FunctionalityService>()
    }
}

val functionalityService get() = FunctionalityService.INSTANCE