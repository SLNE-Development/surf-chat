package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.ChatMotdService
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services

@AutoService(ChatMotdService::class)
class FallbackChatMotdService : ChatMotdService, Services.Fallback {
    override fun loadMotd() {
        TODO("Not yet implemented")
    }

    override fun saveMotd() {
        TODO("Not yet implemented")
    }

    override fun enableMotd() {
        TODO("Not yet implemented")
    }

    override fun disableMotd() {
        TODO("Not yet implemented")
    }

    override fun isMotdEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getMotd(): Component {
        TODO("Not yet implemented")
    }

    override fun setMotdLine(line: Int, message: String) {
        TODO("Not yet implemented")
    }

    override fun clearMotdLine(line: Int) {
        TODO("Not yet implemented")
    }
}