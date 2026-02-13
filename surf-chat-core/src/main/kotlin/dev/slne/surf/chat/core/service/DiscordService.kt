package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

interface DiscordService {
    suspend fun sendCommunityBanNotification(url: String, userUuid: UUID, denylistEntry: DenylistEntry)

    companion object {
        val INSTANCE = requiredService<DiscordService>()
    }
}

val discordService get() = DiscordService.INSTANCE