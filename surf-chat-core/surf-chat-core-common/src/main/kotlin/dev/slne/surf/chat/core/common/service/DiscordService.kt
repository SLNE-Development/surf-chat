package dev.slne.surf.chat.core.common.service

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.entry.DenylistEntry
import dev.slne.surf.surfapi.core.api.util.requiredService

interface DiscordService {
    suspend fun sendCommunityBanNotification(url: String, user: User, denylistEntry: DenylistEntry)

    companion object {
        val INSTANCE = requiredService<DiscordService>()
    }
}

val discordService get() = DiscordService.INSTANCE