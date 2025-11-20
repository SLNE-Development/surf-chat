package dev.slne.surf.chat.core.common.service

import dev.slne.surf.chat.api.entry.DenylistEntry
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.surfapi.core.api.util.requiredService

interface DiscordService {
    suspend fun sendCommunityBanNotification(
        url: String,
        user: CloudPlayer,
        denylistEntry: DenylistEntry
    )

    companion object {
        val INSTANCE = requiredService<DiscordService>()
    }
}

val discordService get() = DiscordService.INSTANCE