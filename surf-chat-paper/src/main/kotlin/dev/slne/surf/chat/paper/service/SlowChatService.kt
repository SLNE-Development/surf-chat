package dev.slne.surf.chat.paper.service

import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

object SlowChatService {
    private val slowChat: AtomicBoolean = AtomicBoolean(false)
    private val lastChecked = ConcurrentHashMap<UUID, Long>()

    fun isSlowChat(): Boolean {
        return slowChat.get()
    }

    fun setSlowChat(enabled: Boolean) {
        slowChat.set(enabled)
        handleSlowChatModeChange(enabled)
    }

    fun checkPlayer(player: Player): Boolean {
        val uuid = player.uniqueId

        if (!isSlowChat()) {
            return true
        }

        if (player.hasPermission(PermissionRegistry.SLOW_CHAT_BYPASS)) {
            return true
        }

        val now = System.currentTimeMillis()
        val lastTime = lastChecked[uuid] ?: 0L

        if (now - lastTime < 30_000) {
            return false
        }

        lastChecked[uuid] = now
        return true
    }

    private fun handleSlowChatModeChange(enabled: Boolean) {
        Bukkit.broadcast(buildText {
            appendInfoPrefix()
            spacer("-".repeat(30))

            appendNewInfoPrefixedLine()

            appendNewInfoPrefixedLine()
            if (enabled) {
                info("Der Slow Chat wurde aktiviert,")
                appendNewInfoPrefixedLine()
                variableValue("du kannst nur alle 30 Sekunden eine Nachricht senden.")
            } else {
                info("Der Slow Chat wurde deaktiviert,")
                appendNewInfoPrefixedLine()
                variableValue("du kannst wieder normal chatten.")
            }
            appendNewInfoPrefixedLine()

            appendInfoPrefix()
            spacer("-".repeat(30))
        })
    }
}