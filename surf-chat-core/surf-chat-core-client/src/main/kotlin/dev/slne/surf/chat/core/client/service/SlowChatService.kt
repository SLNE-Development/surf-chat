package dev.slne.surf.chat.core.client.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import dev.slne.surf.chat.core.client.service.SlowChatService.slowChatInterval
import net.kyori.adventure.text.format.TextDecoration
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.seconds

object SlowChatService {
    private val slowChatInterval = 30.seconds

    private val slowChat: AtomicBoolean = AtomicBoolean(false)

    /**
     * Players that already sent a message within [slowChatInterval].
     */
    private val cooldowns = Caffeine.newBuilder()
        .expireAfterWrite(slowChatInterval)
        .build<UUID, Boolean>()
        .asMap()

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

        return cooldowns.putIfAbsent(uuid, true) == null
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
