package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component
import java.util.UUID

interface ChatMotdService {
    fun loadMotd()
    fun saveMotd()

    fun getMotd(): Component
    fun setMotdLine(line: Int, message: String)

    companion object {
        val INSTANCE = requiredService<ChatMotdService>()
    }
}

val chatMotdService get() = ChatMotdService.INSTANCE