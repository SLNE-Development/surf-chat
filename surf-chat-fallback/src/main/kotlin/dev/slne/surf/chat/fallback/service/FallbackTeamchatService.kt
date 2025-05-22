package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.TeamchatService
import net.kyori.adventure.util.Services

@AutoService(TeamchatService::class)
class FallbackTeamchatService : TeamchatService, Services.Fallback {
}