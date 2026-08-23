@file:Suppress("RETURN_IN_FUNCTION_WITH_EXPRESSION_BODY_WARNING")

package dev.slne.surf.chat.paper.message

import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.plain
import dev.slne.surf.api.core.messages.adventure.playSound
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.client.config.chatConfig
import dev.slne.surf.chat.core.client.hook.SettingsHook
import dev.slne.surf.chat.core.client.message.format.appendDelete
import dev.slne.surf.chat.core.client.message.format.appendMessageData
import dev.slne.surf.chat.core.client.message.format.appendTeleport
import dev.slne.surf.chat.core.client.util.updateLinks
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.appendName
import dev.slne.surf.core.api.paper.CorePlayerStatusAccess
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.regex.Pattern

/**
 * Formats messages that require access to Paper specific player state.
 */
object MessageFormatter {
    private val itemRegex = Regex("\\[(?i)item]")

    private class MentionCache(
        val generation: Long,
        val pattern: Pattern?,
        val playersByName: Map<String, Player>
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
        val senderPlayer = server.getPlayer(messageData.sender) ?: return Component.empty()
        val viewerPlayer = server.getPlayer(viewer)

        if (viewerPlayer != null && viewerPlayer.hasPermission(PermissionRegistry.COMMAND_SURFCHAT_DELETE)) {
            appendDelete(messageData)
        }

        if (viewerPlayer != null && viewerPlayer.hasPermission(PermissionRegistry.COMMAND_SURFCHAT_TELEPORT)) {
            appendTeleport(senderPlayer.name, senderPlayer.uniqueId)
        }

        appendName(senderPlayer)
        darkSpacer(" >> ")

        var content = messageData.message
        if (highlightMentions && viewerPlayer != null) {
            content = highlightPlayers(content, viewer, viewerPlayer)
        }

        append(formatItemTag(updateLinks(content), senderPlayer, viewer, messageData.plainMessage))
        hoverEvent(buildText { appendMessageData(senderPlayer.name, messageData) })
        clickSuggestsCommand("/msg ${senderPlayer.name} ")
    }

    private fun formatItemTag(
        rawMessage: Component,
        player: Player,
        viewer: UUID?,
        sourcePlain: String
    ): Component {
        if (!chatConfig.itemPlaceholder) {
            return rawMessage
        }

        if (!itemRegex.containsMatchIn(sourcePlain)) {
            return rawMessage
        }

        var message = rawMessage

        if (!itemRegex.containsMatchIn(message.plain())) {
            return message
        }

        val item = player.inventory.itemInMainHand

        if (item.type == Material.AIR) {
            if (viewer == null || player.uniqueId == viewer) {
                player.sendText {
                    appendErrorPrefix()
                    error("Du hast kein Item in der Hand!")
                }
            }

            return message
        }

        message = message.replaceText(
            TextReplacementConfig
                .builder()
                .match(itemRegex.pattern)
                .replacement(buildText {
                    if (item.amount > 1) {
                        variableValue("${item.amount}x ")
                    }
                    append(item.displayName())
                })
                .build()
        )

        return message
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

        val players = server.onlinePlayers
        val playersByName = Object2ObjectOpenHashMap<String, Player>(players.size * 2)
        val patternBuilder = StringBuilder(players.size * 24)

        for (player in players) {
            val name = player.name
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

    private fun highlightPlayers(
        rawMessage: Component,
        viewerUuid: UUID,
        viewer: Player
    ): Component {
        val cache = mentionCache()
        val pattern = cache.pattern ?: return rawMessage
        val players = cache.playersByName

        if (players.values.none { CorePlayerStatusAccess.hasAccess(viewer, it) }) {
            return rawMessage
        }

        var viewerMentioned = false

        val message = rawMessage.replaceText(
            TextReplacementConfig.builder()
                .match(pattern)
                .replacement { match ->
                    val fullMatch = match.build().plain()
                    val name = fullMatch.removePrefix("@").lowercase()

                    val player = players[name] ?: return@replacement match

                    if (!CorePlayerStatusAccess.hasAccess(viewer, player)) {
                        return@replacement match
                    }

                    if (player.uniqueId == viewerUuid) {
                        viewerMentioned = true
                    }

                    buildText {
                        text("@")
                        text(player.name)
                        color(Colors.VARIABLE_VALUE)
                        decorate(TextDecoration.BOLD)
                        clickSuggestsCommand("/msg ${player.name} ")
                    }
                }
                .build()
        )

        if (viewerMentioned) {
            if (plugin.checkSettingsHook() && SettingsHook.hasChatPingsEnabled(viewerUuid)) {
                viewer.playSound(true) {
                    type(Sound.ENTITY_CHICKEN_EGG)
                }
            }
        }

        return message
    }
}
