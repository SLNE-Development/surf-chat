package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.ConnectionService
import dev.slne.surf.chat.fallback.config.FallbackChatConfig
import dev.slne.surf.surfapi.core.api.config.surfConfigApi
import net.kyori.adventure.util.Services

@AutoService(ConnectionService::class)
class FallbackConnectionService : ConnectionService, Services.Fallback {
    var config: FallbackChatConfig? = null

    override fun loadMessages() {
        config = surfConfigApi.getSpongeConfig(FallbackChatConfig::class.java)
    }

    override fun reloadMessages() {
        config = surfConfigApi.reloadSpongeConfig(FallbackChatConfig::class.java)
    }

    override fun getJoinMessage(): String {
        return config?.connectionConfig?.joinFormat ?: error("Invalid or unloaded configuration.")
    }

    override fun getLeaveMessage(): String {
        return config?.connectionConfig?.leaveFormat ?: error("Invalid or unloaded configuration.")
    }

    override fun isEnabled(): Boolean {
        return config?.connectionConfig?.enabled ?: false
    }
}