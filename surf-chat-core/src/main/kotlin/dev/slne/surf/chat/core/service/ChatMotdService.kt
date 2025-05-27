package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component
import java.nio.file.Path

interface ChatMotdService {
    fun loadMotd()
    fun isMotdEnabled(): Boolean
    fun getMotd(): Component

    companion object {
        val INSTANCE = requiredService<ChatMotdService>()
    }
}

val chatMotdService get() = ChatMotdService.INSTANCE