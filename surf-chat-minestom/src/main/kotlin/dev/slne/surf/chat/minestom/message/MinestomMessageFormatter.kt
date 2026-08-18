@file:Suppress("RETURN_IN_FUNCTION_WITH_EXPRESSION_BODY_WARNING")

package dev.slne.surf.chat.minestom.message

import dev.slne.minestom.lobby.api.extension.ConnectionManager
import dev.slne.minestom.lobby.api.player.LobbyPlayer
import dev.slne.minestom.lobby.api.player.getOnlineLobbyPlayerByUuid
import dev.slne.minestom.lobby.api.player.onlineLobbyPlayers
import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.*
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.client.hook.LuckPermsHook
import dev.slne.surf.chat.core.client.hook.SettingsHook
import dev.slne.surf.chat.core.client.message.format.appendDelete
import dev.slne.surf.chat.core.client.message.format.appendMessageData
import dev.slne.surf.chat.core.client.message.format.appendName
import dev.slne.surf.chat.core.client.message.format.appendTeleport
import dev.slne.surf.chat.core.client.permission.ChatPermissions
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import dev.slne.surf.chat.core.client.util.hasPermission
import dev.slne.surf.chat.core.client.util.updateLinks
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextDecoration
import java.util.*

/**
 * Formats messages that require access to Minestom specific player state.
 */
object MinestomMessageFormatter {

    @Volatile
    var cachedRegex: Regex? = null

    @Volatile
    var cachedPlayerMap: Map<String, LobbyPlayer> = emptyMap()

    @Volatile
    var dirty = true

    fun formatGlobal(messageData: MessageData) = buildText {
        val viewer = messageData.receiver ?: return Component.empty()
        val senderPlayer = ConnectionManager.getOnlineLobbyPlayerByUuid(messageData.sender)
            ?: return Component.empty()

        if (viewer.hasPermission(ChatPermissions.COMMAND_SURFCHAT_DELETE)) {
            appendDelete(messageData)
        }

        if (viewer.hasPermission(ChatPermissions.COMMAND_SURFCHAT_TELEPORT)) {
            appendTeleport(senderPlayer.username, senderPlayer.uuid)
        }

        appendName(senderPlayer.username, LuckPermsHook.getPrefix(senderPlayer.uuid))
        darkSpacer(" >> ")
        append(updateLinks(highlightPlayers(messageData.message, viewer)))
        hoverEvent(buildText { appendMessageData(senderPlayer.username, messageData) })
        clickSuggestsCommand("/msg ${senderPlayer.username} ")
    }

    private fun ensureMentionCache() {
        if (!dirty) return

        val players = ConnectionManager.onlineLobbyPlayers
        dirty = false

        if (players.isEmpty()) {
            cachedRegex = null
            cachedPlayerMap = emptyMap()
            return
        }

        cachedPlayerMap = players.associateBy { it.username.lowercase() }

        val pattern = players.joinToString("|") {
            Regex.escape(it.username)
        }

        cachedRegex = Regex("@?($pattern)\\b", RegexOption.IGNORE_CASE)
    }

    private fun highlightPlayers(rawMessage: Component, viewerUuid: UUID): Component {
        ensureMentionCache()
        ConnectionManager.getOnlineLobbyPlayerByUuid(viewerUuid) ?: return rawMessage

        val regex = cachedRegex ?: return rawMessage
        val players = cachedPlayerMap

        if (players.isEmpty()) return rawMessage

        val plain = rawMessage.plain()
        if (!regex.containsMatchIn(plain)) return rawMessage

        var viewerMentioned = false

        val message = rawMessage.replaceText(
            TextReplacementConfig.builder()
                .match(regex.toPattern())
                .replacement { match ->
                    val fullMatch = match.build().plain()
                    val name = fullMatch.removePrefix("@").lowercase()

                    val player = players[name] ?: return@replacement match

                    if (player.uuid == viewerUuid) {
                        viewerMentioned = true
                    }

                    buildText {
                        text("@")
                        text(player.username)
                        color(Colors.VARIABLE_VALUE)
                        decorate(TextDecoration.BOLD)
                        clickSuggestsCommand("/msg ${player.username} ")
                    }
                }
                .build()
        )

        if (viewerMentioned && SettingsHook.hasChatPingsEnabled(viewerUuid)) {
            ChatPlatform.playPingSound(viewerUuid)
        }

        return message
    }
}
