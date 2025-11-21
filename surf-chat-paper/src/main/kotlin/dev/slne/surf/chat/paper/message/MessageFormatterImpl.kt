package dev.slne.surf.chat.paper.message

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.chat.core.common.message.MessageData
import dev.slne.surf.chat.core.common.message.MessageFormatter
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.util.*
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickOpensUrl
import dev.slne.surf.surfapi.core.api.messages.adventure.clickSuggestsCommand
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.minutes

class MessageFormatterImpl(override val message: Component) : MessageFormatter {
    private val linkRegex = Regex("(?i)\\b((https?://)?[\\w-]+(\\.[\\w-]+)+(/\\S*)?)\\b")
    private val itemRegex = Regex("\\[(?i)item]")
    private val nameRegexCache = Caffeine.newBuilder()
        .expireAfterWrite(15.minutes)
        .build<String, Regex> { name ->
            Regex("\\b@?${Regex.escape(name)}\\b")
        }

    override fun formatGlobal(messageData: MessageData) = buildText {
        val viewer = messageData.receiver ?: return Component.empty()
        val player = messageData.sender
        val bukkitPlayer = player.bukkitPlayer ?: return@buildText

        if (viewer.hasPlatformPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_DELETE)) {
            appendDelete(messageData)
        }

        if (viewer.hasPlatformPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_TELEPORT)) {
            appendTeleport(messageData.sender.name, viewer.bukkitPlayer ?: return@buildText)
        }

        appendName(bukkitPlayer)
        darkSpacer(" >> ")
        append(
            formatItemTag(
                updateLinks(highlightPlayers(messageData.message, viewer)),
                bukkitPlayer
            )
        )
        hoverEvent(buildText { appendMessageData(messageData) })
        clickSuggestsCommand("/msg ${player.name} ")
    }

    override fun formatIncomingPm(messageData: MessageData) = buildText {
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

    override fun formatOutgoingPm(messageData: MessageData) = buildText {
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

    override fun formatTeamchat(messageData: MessageData) = buildText {
        val player = messageData.sender.bukkitPlayer ?: return Component.empty()

        darkSpacer(">> ")
        text("TEAM", Colors.RED, TextDecoration.BOLD)
        darkSpacer(" | ")
        appendName(player)
        darkSpacer(" >> ")
        append(updateLinks(formatItemTag(messageData.message, player)))

        hoverEvent(buildText { appendMessageData(messageData) })
        clickSuggestsCommand("/teamchat ")
    }

    override fun formatChannel(messageData: MessageData) = buildText {
        val player = messageData.sender.bukkitPlayer ?: return Component.empty()
        val receiver = messageData.receiver?.bukkitPlayer ?: return Component.empty()

        if (receiver.hasPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_DELETE)) {
            appendDelete(messageData)
        }

        if (receiver.hasPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_TELEPORT)) {
            appendTeleport(messageData.sender.name, receiver)
        }

        appendChannelPrefix(messageData.channel?.channelName ?: "Unbekannter Kanal")
        appendName(player)
        darkSpacer(" >> ")
        append(updateLinks(formatItemTag(messageData.message, player)))
        hoverEvent(buildText { appendMessageData(messageData) })
        clickSuggestsCommand("/msg ${player.name} ")
    }

    override fun formatPmSpy(messageData: MessageData) = buildText {
        val receiver = messageData.receiver?.bukkitPlayer ?: return Component.empty()

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

    override fun formatChannelSpy(messageData: MessageData) = buildText {
        val player = messageData.sender.bukkitPlayer ?: return Component.empty()
        val receiver = messageData.receiver?.bukkitPlayer ?: return Component.empty()

        appendSpyIcon()
        appendSpace()

        if (receiver.hasPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_DELETE)) {
            appendDelete(messageData)
        }

        if (receiver.hasPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_TELEPORT)) {
            appendTeleport(messageData.sender.name, receiver)
        }

        appendChannelPrefix(messageData.channel?.channelName ?: "Unbekannter Kanal")
        appendName(player)
        darkSpacer(" >> ")
        append(updateLinks(formatItemTag(messageData.message, player)))
        hoverEvent(buildText { appendMessageData(messageData) })
        clickSuggestsCommand("/msg ${player.name} ")
    }


    private fun formatItemTag(rawMessage: Component, player: Player): Component {
        var message = rawMessage
        val item = player.inventory.itemInMainHand

        if (!itemRegex.containsMatchIn(message.plainText())) {
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

    private fun highlightPlayers(rawMessage: Component, viewer: CloudPlayer): Component {
        var message = rawMessage

        val name = viewer.name
        val pattern = nameRegexCache.get(name)

        if (!pattern.containsMatchIn(message.plainText())) {
            return message
        }

        message = message.replaceText(
            TextReplacementConfig.builder()
                .match(pattern.pattern)
                .replacement { matchResult ->
                    val matchedText = matchResult.content()
                    buildText {
                        text(matchedText)
                        decorate(TextDecoration.BOLD)
                    }
                }
                .build()
        )
        //TODO: surf-settings
//        plugin.launch(Dispatchers.IO) {
//            if (viewer.configure().pingsEnabled()) {
//                viewerPlayer.playSound(sound {
//                    type(Sound.BLOCK_NOTE_BLOCK_HARP)
//                    source(net.kyori.adventure.sound.Sound.Source.PLAYER)
//                    volume(1f)
//                    pitch(2f)
//                }, net.kyori.adventure.sound.Sound.Emitter.self())
//            }
//        }

        return message
    }

    private fun updateLinks(rawMessage: Component): Component {
        var message = rawMessage
        val text = rawMessage.plainText()

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