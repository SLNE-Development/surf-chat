package dev.slne.surf.chat.bukkit.message

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.*
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.minutes

/**
 * Interface for formatting messages in various contexts within the chat system.
 *
 * This interface defines methods for formatting global, private, team, channel, and spy messages.
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

        if (viewer.hasPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_DELETE)) {
            appendDelete(messageData)
        }

        if (viewer.hasPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_TELEPORT)) {
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

    fun formatChannel(messageData: MessageData) = buildText {
        val player = messageData.sender.player() ?: return Component.empty()
        val receiver = messageData.receiver ?: return Component.empty()

        if (receiver.hasPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_DELETE)) {
            appendDelete(messageData)
        }

        if (receiver.hasPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_TELEPORT)) {
            appendTeleport(messageData.sender.name, receiver)
        }

        appendChannelPrefix(messageData.channel ?: "Unbekannter Kanal")
        appendName(player)
        darkSpacer(" >> ")
        append(updateLinks(formatItemTag(messageData.message, player)))
        hoverEvent(buildText { appendMessageData(messageData) })
        clickSuggestsCommand("/msg ${player.name} ")
    }

    fun formatPmSpy(messageData: MessageData) = buildText {
        val receiver = messageData.receiver ?: return Component.empty()

        appendSpyIcon()
        appendSpace()

        if (receiver.hasPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_TELEPORT)) {
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

    fun formatChannelSpy(messageData: MessageData) = buildText {
        val player = messageData.sender.player() ?: return Component.empty()
        val receiver = messageData.receiver ?: return Component.empty()

        appendSpyIcon()
        appendSpace()

        if (receiver.hasPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_DELETE)) {
            appendDelete(messageData)
        }

        if (receiver.hasPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_TELEPORT)) {
            appendTeleport(messageData.sender.name, receiver)
        }

        appendChannelPrefix(messageData.channel ?: "Unbekannter Kanal")
        appendName(player)
        darkSpacer(" >> ")
        append(updateLinks(formatItemTag(messageData.message, player)))
        hoverEvent(buildText { appendMessageData(messageData) })
        clickSuggestsCommand("/msg ${player.name} ")
    }


    private fun formatItemTag(rawMessage: Component, player: Player): Component {
        var message = rawMessage
        val item = player.inventory.itemInMainHand

        if (!itemRegex.containsMatchIn(message.plain())) {
            return message
        }

        if (item.type == Material.AIR) {
            player.sendText {
                appendPrefix()
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

    private fun highlightPlayers(rawMessage: Component, viewer: User): Component {
        var message = rawMessage

        val name = viewer.name
        val pattern = nameRegexCache.get(name)

        if (!pattern.containsMatchIn(message.plain())) {
            return message
        }

        message = message.replaceText(
            TextReplacementConfig.builder()
                .match(pattern.pattern)
                .replacement { matchResult ->
                    val matchedText = matchResult.content()
                    val displayName = if (matchedText.startsWith("@")) {
                        matchedText
                    } else {
                        "@$matchedText"
                    }

                    buildText {
                        text(displayName)
                        color(Colors.VARIABLE_VALUE)
                        decorate(TextDecoration.BOLD)
                    }
                }
                .build()
        )

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
}