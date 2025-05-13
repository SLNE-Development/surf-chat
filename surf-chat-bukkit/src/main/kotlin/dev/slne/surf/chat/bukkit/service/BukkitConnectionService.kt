package dev.slne.surf.chat.bukkit.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.bukkit.util.pluginConfig
import dev.slne.surf.chat.core.service.ConnectionService
import net.kyori.adventure.util.Services

@AutoService(ConnectionService::class)
class BukkitConnectionService: ConnectionService, Services.Fallback {
    private var joinMessage: String = "An internal error occurred: 011"
    private var leaveMessage: String = "An internal error occurred: 012."
    private var enabled: Boolean = true

    override fun loadMessages() {
        joinMessage = pluginConfig.getString("connection.messages.join") ?: "An internal error occurred: 021"
        leaveMessage = pluginConfig.getString("connection.messages.leave") ?: "An internal error occurred: 022."
        enabled = pluginConfig.getBoolean("connection.messages.enabled")
    }

    override fun reloadMessages() {
        this.loadMessages()
    }

    override fun getJoinMessage(): String {
        return joinMessage
    }

    override fun getLeaveMessage(): String {
        return leaveMessage
    }

    override fun isEnabled(): Boolean {
        return enabled
    }
}