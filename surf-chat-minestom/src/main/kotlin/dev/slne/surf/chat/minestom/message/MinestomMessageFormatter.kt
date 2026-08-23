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
import dev.slne.surf.chat.core.client.util.updateLinks
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextDecoration
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.regex.Pattern

/**
 * Formats messages that require access to Minestom specific player state.
 */
object MinestomMessageFormatter {

    private class MentionCache(
        val generation: Long,
        val pattern: Pattern?,
        val playersByName: Map<String, LobbyPlayer>
    )

    private val mentionCacheLock = Any()
    private val mentionGeneration = AtomicLong()

    @Volatile
    private var mentionCache: MentionCache? = null

    /**
     * Marks the mention cache stale; the next render rebuilds it.
     */
    fun invalidateMentionCache() {
        mentionGeneration.incrementAndGet()
    }

    /**
     * Whether [plainMessage] mentions any online player.
     */
    fun hasMention(plainMessage: String): Boolean {
        val pattern = mentionCache().pattern ?: return false
        return pattern.matcher(plainMessage).find()
    }

    fun formatGlobal(messageData: MessageData): TextComponent {
        val viewer = messageData.receiver ?: return Component.empty()
        return formatGlobal(messageData, viewer, hasMention(messageData.plainMessage))
    }

    fun formatGlobal(messageData: MessageData, viewer: UUID, highlightMentions: Boolean) = buildText {
        val senderPlayer = ConnectionManager.getOnlineLobbyPlayerByUuid(messageData.sender)
            ?: return Component.empty()

        val viewerPlayer = ConnectionManager.getOnlineLobbyPlayerByUuid(viewer)

        if (viewerPlayer != null && viewerPlayer.hasPermission(ChatPermissions.COMMAND_SURFCHAT_DELETE)) {
            appendDelete(messageData)
        }

        if (viewerPlayer != null && viewerPlayer.hasPermission(ChatPermissions.COMMAND_SURFCHAT_TELEPORT)) {
            appendTeleport(senderPlayer.username, senderPlayer.uuid)
        }

        appendName(senderPlayer.username, LuckPermsHook.getPrefix(senderPlayer.uuid))
        darkSpacer(" >> ")

        var content = messageData.message
        if (highlightMentions && viewerPlayer != null) {
            content = highlightPlayers(content, viewer)
        }

        append(updateLinks(content))
        hoverEvent(buildText { appendMessageData(senderPlayer.username, messageData) })
        clickSuggestsCommand("/msg ${senderPlayer.username} ")
    }

    private fun mentionCache(): MentionCache {
        val generation = mentionGeneration.get()
        val cached = mentionCache

        if (cached != null && cached.generation == generation) {
            return cached
        }

        return rebuildMentionCache()
    }

    private fun rebuildMentionCache(): MentionCache = synchronized(mentionCacheLock) {
        val generation = mentionGeneration.get()
        val cached = mentionCache

        if (cached != null && cached.generation == generation) {
            return cached
        }

        val players = ConnectionManager.onlineLobbyPlayers
        val playersByName = Object2ObjectOpenHashMap<String, LobbyPlayer>(players.size * 2)
        val patternBuilder = StringBuilder(players.size * 24)

        for (player in players) {
            val name = player.username
            playersByName[name.lowercase()] = player

            if (patternBuilder.isNotEmpty()) {
                patternBuilder.append('|')
            }
            patternBuilder.append(Pattern.quote(name))
        }

        val rebuilt = MentionCache(
            generation,
            if (players.isEmpty()) null else Pattern.compile(
                "@?($patternBuilder)\\b",
                Pattern.CASE_INSENSITIVE
            ),
            playersByName
        )

        mentionCache = rebuilt
        rebuilt
    }

    private fun highlightPlayers(rawMessage: Component, viewerUuid: UUID): Component {
        val cache = mentionCache()
        val pattern = cache.pattern ?: return rawMessage
        val players = cache.playersByName

        var viewerMentioned = false

        val message = rawMessage.replaceText(
            TextReplacementConfig.builder()
                .match(pattern)
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
