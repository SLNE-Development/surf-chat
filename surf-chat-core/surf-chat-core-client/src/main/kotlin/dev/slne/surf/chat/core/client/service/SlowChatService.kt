package dev.slne.surf.chat.core.client.service

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import net.kyori.adventure.text.format.TextDecoration
import java.util.UUID
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

    fun checkPlayer(uuid: UUID): Boolean {
        if (!isSlowChat()) {
            return true
        }

        if (ChatPlatform.hasPermission(uuid, ChatPermissions.SLOW_CHAT_BYPASS)) {
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
        ChatPlatform.broadcast(buildText {
            appendInfoPrefix()
            spacer("-".repeat(30))

            appendNewInfoPrefixedLine()

            appendNewInfoPrefixedLine()
            if (enabled) {
                info("Der Slow Chat wurde aktiviert,".toSmallCaps())
                appendNewInfoPrefixedLine()
                variableValue("du kannst nur alle 30 Sekunden ".toSmallCaps(), TextDecoration.BOLD)
                appendNewInfoPrefixedLine()
                variableValue("eine Nachricht senden.".toSmallCaps(), TextDecoration.BOLD)
            } else {
                info("Der Slow Chat wurde deaktiviert,".toSmallCaps())
                appendNewInfoPrefixedLine()
                variableValue("du kannst wieder normal chatten.".toSmallCaps(), TextDecoration.BOLD)
            }
            appendNewInfoPrefixedLine()

            appendNewInfoPrefixedLine()
            spacer("-".repeat(30))
        })
    }
}
