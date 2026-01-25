@file:Suppress("RETURN_IN_FUNCTION_WITH_EXPRESSION_BODY_WARNING")

package dev.slne.surf.chat.bukkit.message

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.bukkit.hook.SettingsHook
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.*
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.minutes

/**
 * Interface for formatting messages in various contexts within the chat system.
 *
 * This interface defines methods for formatting global, private, team, and spy messages.
 * Each method accepts message data and returns a formatted `Component` object suitable for the
 * specific context in which the message will be displayed or processed.
 */
class MessageFormatter {
    private val linkRegex = Regex("(?i)\\b((https?://)?[\\w-]+(\\.[\\w-]+)+(/\\S*)?)\\b")
    private val itemRegex = Regex("\\[(?i)item]")
    private val nameRegexCache = Caffeine.newBuilder()
        .expireAfterWrite(15.minutes)
        .build<String, Regex> { name ->
            Regex("\\b@?${Regex.escape(name)}\\b")
        }

    fun formatGlobal(messageData: MessageData) = buildText {
        val viewer = messageData.receiver ?: return Component.empty()
        val player = messageData.sender.player() ?: return Component.empty()

        if (viewer.hasPermission(PermissionRegistry.COMMAND_SURFCHAT_DELETE)) {
            appendDelete(messageData)
        }

        if (viewer.hasPermission(PermissionRegistry.COMMAND_SURFCHAT_TELEPORT)) {
            appendTeleport(messageData.sender.name, viewer)
        }

        appendName(player)
        darkSpacer(" >> ")
        append(
            formatItemTag(
                updateLinks(highlightPlayers(messageData.message, viewer)),
                player
            )
        )
        hoverEvent(buildText { appendMessageData(messageData) })
        clickSuggestsCommand("/msg ${player.name} ")
    }

    fun formatIncomingPm(messageData: MessageData) = buildText {
        val senderName = messageData.sender.name

        darkSpacer(">> ")
        text("PM", Colors.RED)
        darkSpacer(" | ")
        variableValue(senderName)
        darkSpacer(" -> ")
        variableValue("Dir")
        darkSpacer(" >> ")
        append(updateLinks(messageData.message))
        hoverEvent(buildText { appendMessageData(messageData) })
        clickSuggestsCommand("/msg $senderName ")
    }

    fun formatOutgoingPm(messageData: MessageData) = buildText {
        val receiverName = messageData.receiver?.name ?: "Error"

        darkSpacer(">> ")
        text("PM", Colors.RED)
        darkSpacer(" | ")
        variableValue("Du")
        darkSpacer(" -> ")
        variableValue(receiverName)
        darkSpacer(" >> ")
        append(updateLinks(messageData.message))

        hoverEvent(buildText { appendMessageData(messageData) })
        clickSuggestsCommand("/msg $receiverName ")
    }

    fun formatTeamchat(messageData: MessageData) = buildText {
        val player = messageData.sender.player() ?: return Component.empty()

        darkSpacer(">> ")
        text("TEAM", Colors.RED, TextDecoration.BOLD)
        darkSpacer(" | ")
        appendName(player)
        darkSpacer(" >> ")
        append(updateLinks(formatItemTag(messageData.message, player)))

        hoverEvent(buildText { appendMessageData(messageData) })
        clickSuggestsCommand("/teamchat ")
    }

    fun formatPmSpy(messageData: MessageData) = buildText {
        val receiver = messageData.receiver ?: return Component.empty()

        appendSpyIcon()
        appendSpace()

        if (receiver.hasPermission(PermissionRegistry.COMMAND_SURFCHAT_TELEPORT)) {
            appendTeleport(messageData.sender.name, receiver)
        }

        variableValue(messageData.sender.name)
        appendSpace()
        darkSpacer("-->")
        appendSpace()
        variableValue(receiver.name)
        spacer(":")
        appendSpace()
        append(updateLinks(messageData.message))
        hoverEvent(buildText { appendMessageData(messageData) })
        clickSuggestsCommand("/msg ${messageData.sender.name} ")
    }


    private fun formatItemTag(rawMessage: Component, player: Player): Component {
        if (!plugin.surfChatConfig.config.itemPlaceholder) {
            return rawMessage
        }

        var message = rawMessage
        val item = player.inventory.itemInMainHand

        if (!itemRegex.containsMatchIn(message.plain())) {
            return message
        }

        if (item.type == Material.AIR) {
            player.sendText {
                appendErrorPrefix()
                error("Du hast kein Item in der Hand!")
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

    private fun ensureMentionCache() {
        if (!dirty) return

        val players = Bukkit.getOnlinePlayers()
        dirty = false

        if (players.isEmpty()) {
            cachedRegex = null
            cachedPlayerMap = emptyMap()
            return
        }

        cachedPlayerMap = players.associateBy { it.name.lowercase() }

        val pattern = players.joinToString("|") {
            Regex.escape(it.name)
        }

        cachedRegex = Regex("\\b@?($pattern)\\b", RegexOption.IGNORE_CASE)
    }


    private fun highlightPlayers(rawMessage: Component, viewer: User): Component {
        ensureMentionCache()

        val regex = cachedRegex ?: return rawMessage
        val playerMap = cachedPlayerMap

        val plain = rawMessage.plain()
        if (!regex.containsMatchIn(plain)) return rawMessage

        var viewerMentioned = false

        val message = rawMessage.replaceText(
            TextReplacementConfig.builder()
                .match(regex.pattern)
                .replacement { match ->
                    val raw = match.build().plain()
                    val clean = raw.removePrefix("@").lowercase()
                    val player = playerMap[clean] ?: return@replacement match

                    if (player.uniqueId == viewer.uuid) {
                        viewerMentioned = true
                    }

                    buildText {
                        text("@${player.name}")
                        color(Colors.VARIABLE_VALUE)
                        decorate(TextDecoration.BOLD)
                    }
                }
                .build()
        )

        if (viewerMentioned && SettingsHook.hasChatPingsEnabled(viewer.uuid)) {
            Bukkit.getPlayer(viewer.uuid)?.playSound(true) {
                type(Sound.BLOCK_NOTE_BLOCK_HARP)
            }
        }

        return message
    }


    private fun updateLinks(rawMessage: Component): Component {
        var message = rawMessage
        val text = rawMessage.plain()

        linkRegex.findAll(text).filter { text.contains(it.value) }.forEach {
            message = message.replaceText(
                TextReplacementConfig.builder()
                    .match(Regex.escape(it.value))
                    .replacement(
                        buildText {
                            text(it.value)
                            hoverEvent(buildText {
                                info("Klicke hier, um den Link zu öffnen.")
                            })
                            clickOpensUrl(it.value)
                        }
                    )
                    .build()
            )
        }

        return message
    }

    companion object {
        @Volatile
        var cachedRegex: Regex? = null

        @Volatile
        var cachedPlayerMap: Map<String, Player> = emptyMap()

        @Volatile
        var dirty = true

    }
}