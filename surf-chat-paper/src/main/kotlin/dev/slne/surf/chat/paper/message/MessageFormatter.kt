@file:Suppress("RETURN_IN_FUNCTION_WITH_EXPRESSION_BODY_WARNING")

package dev.slne.surf.chat.paper.message

import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.*
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.core.client.config.chatConfig
import dev.slne.surf.chat.core.client.message.format.appendDelete
import dev.slne.surf.chat.core.client.message.format.appendMessageData
import dev.slne.surf.chat.core.client.message.format.appendTeleport
import dev.slne.surf.chat.core.client.util.hasPermission
import dev.slne.surf.chat.core.client.util.updateLinks
import dev.slne.surf.chat.core.client.hook.SettingsHook
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.appendName
import dev.slne.surf.core.api.paper.CorePlayerStatusAccess
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*

/**
 * Formats messages that require access to Paper specific player state.
 */
object MessageFormatter {
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

    private fun formatItemTag(rawMessage: Component, player: Player, viewer: UUID?): Component {
        if (!chatConfig.itemPlaceholder) {
            return rawMessage
        }

        var message = rawMessage
        val item = player.inventory.itemInMainHand

        if (!itemRegex.containsMatchIn(message.plain())) {
            return message
        }


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

}
