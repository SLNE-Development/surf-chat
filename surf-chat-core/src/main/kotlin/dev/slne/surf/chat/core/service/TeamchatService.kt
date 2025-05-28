package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component

interface TeamchatService {
    fun sendMessage(component: Component)

    companion object {
        val INSTANCE = requiredService<TeamchatService>()
    }
}

val teamchatService get() = TeamchatService.INSTANCE