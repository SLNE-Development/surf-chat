package dev.slne.surf.chat.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService

interface ChannelService {

    companion object {
        val INSTANCE = requiredService<ChannelService>()
    }
}

val char get() = ChannelService.INSTANCE