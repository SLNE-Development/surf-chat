package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.ConnectionService
import net.kyori.adventure.util.Services

@AutoService(ConnectionService::class)
class FallbackConnectionService : ConnectionService, Services.Fallback {
    override fun loadMessages() {
        TODO("Not yet implemented")
    }

    override fun reloadMessages() {
        TODO("Not yet implemented")
    }

    override fun getJoinMessage(): String {
        TODO("Not yet implemented")
    }

    override fun getLeaveMessage(): String {
        TODO("Not yet implemented")
    }

    override fun isEnabled(): Boolean {
        TODO("Not yet implemented")
    }
}