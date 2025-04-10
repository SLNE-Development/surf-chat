package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component

interface ChatMotdService {
    fun loadMotd()
    fun saveMotd()

    fun enableMotd()
    fun disableMotd()

    fun getMotd(): Component
    fun setMotdLine(line: Int, message: String)

    companion object {
        val INSTANCE = requiredService<ChatMotdService>()
    }
}

val chatMotdService get() = ChatMotdService.INSTANCE