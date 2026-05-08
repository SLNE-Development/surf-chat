@file:Suppress("RETURN_IN_FUNCTION_WITH_EXPRESSION_BODY_WARNING")

package dev.slne.surf.chat.paper.message

import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.*
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.paper.hook.SettingsHook
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.*
import dev.slne.surf.core.api.paper.CorePlayerStatusAccess
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.net.URI
import java.util.*

/**
 * Interface for formatting messages in various contexts within the chat system.
 *
 * This interface defines methods for formatting global, private, team, and spy messages.
 * Each method accepts message data and returns a formatted `Component` object suitable for the
 * specific context in which the message will be displayed or processed.
 */
object MessageFormatter {
    private val linkRegex = Regex("(?i)\\b((https?://)?[\\w-]+(\\.[\\w-]+)+(/\\S*)?)\\b")
    private val itemRegex = Regex("\\[(?i)item]")

    @Volatile
    var cachedRegex: Regex? = null

    @Volatile
    var cachedPlayerMap: Map<String, Player> = emptyMap()

    @Volatile
    var dirty = true

    fun formatGlobal(messageData: MessageData) = buildText {
        val viewer = messageData.receiver ?: return Component.empty()
        val senderPlayer = server.getPlayer(messageData.sender) ?: return Component.empty()

        if (viewer.hasPermission(PermissionRegistry.COMMAND_SURFCHAT_DELETE)) {
            appendDelete(messageData)
        }

        if (viewer.hasPermission(PermissionRegistry.COMMAND_SURFCHAT_TELEPORT)) {
            appendTeleport(senderPlayer.name, senderPlayer.uniqueId)
        }

        appendName(senderPlayer)
        darkSpacer(" >> ")
        append(
            formatItemTag(
                updateLinks(highlightPlayers(messageData.message, viewer)),
                senderPlayer,
                viewer
            )
        )
        hoverEvent(buildText { appendMessageData(senderPlayer.name, messageData) })
        clickSuggestsCommand("/msg ${senderPlayer.name} ")
    }

    suspend fun formatIncomingPm(messageData: MessageData) = buildText {
        val senderUser = messageData.senderUser()

        darkSpacer(">> ")
        text("PM", Colors.RED)
        darkSpacer(" | ")
        variableValue(senderUser.lastKnownName ?: senderUser.uuid.toString())
        darkSpacer(" -> ")
        variableValue("Dir")
        darkSpacer(" >> ")
        append(updateLinks(messageData.message))
        hoverEvent(buildText {
            appendMessageData(
                senderUser.lastKnownName ?: senderUser.uuid.toString(), messageData
            )
        })
        clickSuggestsCommand("/msg ${senderUser.lastKnownName} ")
    }

    suspend fun formatOutgoingPm(messageData: MessageData) = buildText {
        val receiverName = messageData.receiverUser()?.lastKnownName ?: "Error"

        darkSpacer(">> ")
        text("PM", Colors.RED)
        darkSpacer(" | ")
        variableValue("Du")
        darkSpacer(" -> ")
        variableValue(receiverName)
        darkSpacer(" >> ")
        append(updateLinks(messageData.message))

        hoverEvent(buildText { appendMessageData(receiverName, messageData) })
        clickSuggestsCommand("/msg $receiverName ")
    }

    fun formatTeamchat(messageData: MessageData) = buildText {
        val player = Bukkit.getPlayer(messageData.sender) ?: return Component.empty()

        darkSpacer(">> ")
        text("TEAM", Colors.RED, TextDecoration.BOLD)
        darkSpacer(" | ")
        appendName(player)
        darkSpacer(" >> ")
        append(updateLinks(formatItemTag(messageData.message, player, messageData.receiver)))

        hoverEvent(buildText { appendMessageData(player.name, messageData) })
        clickSuggestsCommand("/teamchat ")
    }

    suspend fun formatPmSpy(messageData: MessageData) = buildText {
        val receiver = messageData.receiver ?: return Component.empty()
        val receiverUser = messageData.receiverUser()
        val receiverName = receiverUser?.lastKnownName ?: return Component.empty()
        val senderName = messageData.senderUser().lastKnownName ?: return Component.empty()

        appendSpyIcon()
        appendSpace()

        if (receiver.hasPermission(PermissionRegistry.COMMAND_SURFCHAT_TELEPORT)) {
            appendTeleport(receiverName, receiver)
        }

        variableValue(senderName)
        appendSpace()
        darkSpacer("-->")
        appendSpace()
        variableValue(receiverName)
        spacer(":")
        appendSpace()
        append(updateLinks(messageData.message))
        hoverEvent(buildText { appendMessageData(senderName, messageData) })
        clickSuggestsCommand("/msg $senderName ")
    }


    private fun formatItemTag(rawMessage: Component, player: Player, viewer: UUID?): Component {
        if (!plugin.surfChatConfig.config.itemPlaceholder) {
            return rawMessage
        }

        var message = rawMessage
        val item = player.inventory.itemInMainHand

        if (!itemRegex.containsMatchIn(message.plain())) {
            return message
        }

        if (item.type == Material.AIR) {
            if (player.uniqueId == viewer) {
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

        cachedRegex = Regex("@?($pattern)\\b", RegexOption.IGNORE_CASE)
    }


    private fun highlightPlayers(rawMessage: Component, viewerUuid: UUID): Component {
        ensureMentionCache()
        val viewer = Bukkit.getPlayer(viewerUuid) ?: return rawMessage

        val regex = cachedRegex ?: return rawMessage

        val accessiblePlayers = cachedPlayerMap.filterValues {
            CorePlayerStatusAccess.hasAccess(viewer, it)
        }

        if (accessiblePlayers.isEmpty()) return rawMessage

        val plain = rawMessage.plain()
        if (!regex.containsMatchIn(plain)) return rawMessage

        var viewerMentioned = false

        val message = rawMessage.replaceText(
            TextReplacementConfig.builder()
                .match(regex.toPattern())
                .replacement { match ->
                    val fullMatch = match.build().plain()
                    val name = fullMatch.removePrefix("@").lowercase()

                    val player = accessiblePlayers[name] ?: return@replacement match

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


    private fun updateLinks(rawMessage: Component): Component {
        var message = rawMessage
        val text = rawMessage.plain()

        linkRegex.findAll(text).forEach { match ->
            runCatching {
                val url = if (match.value.startsWith("http://") || match.value.startsWith("https://")) {
                    match.value
                } else {
                    "https://${match.value}"
                }

                val uri = URI(url)
                uri.toURL()

                message = message.replaceText(
                    TextReplacementConfig.builder()
                        .match(Regex.escape(match.value))
                        .replacement(
                            buildText {
                                text(match.value)
                                hoverEvent(buildText {
                                    info("Klicke hier, um den Link zu öffnen.")
                                })
                                clickOpensUrl(url)
                            }
                        )
                        .build()
                )
            }
        }

        return message
    }

}