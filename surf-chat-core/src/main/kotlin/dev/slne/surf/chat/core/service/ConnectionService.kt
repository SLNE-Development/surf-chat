package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService

interface ConnectionService {
    fun loadMessages()
    fun reloadMessages()

    fun getJoinMessage(): String
    fun getLeaveMessage(): String
    fun isEnabled(): Boolean

    companion object {
        val INSTANCE = requiredService<ConnectionService>()
    }
}

val connectionService get() = ConnectionService.INSTANCE