package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService

interface TeamchatService {

    companion object {
        val INSTANCE = requiredService<TeamchatService>()
    }
}

val teamchatService get() = TeamchatService.INSTANCE