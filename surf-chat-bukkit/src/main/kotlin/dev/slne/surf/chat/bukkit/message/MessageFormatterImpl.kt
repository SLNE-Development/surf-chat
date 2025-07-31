package dev.slne.surf.chat.bukkit.message

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.shynixn.mccoroutine.folia.launch
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.components
import dev.slne.surf.chat.bukkit.util.plainText
import dev.slne.surf.chat.bukkit.util.player
import dev.slne.surf.chat.core.message.MessageData
import dev.slne.surf.chat.core.message.MessageFormatter
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.*
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.minutes

class MessageFormatterImpl(override val message: Component) : MessageFormatter {
    override fun formatGlobal(messageData: MessageData) = buildText {
        val viewer = messageData.receiver ?: return Component.empty()
        val player = messageData.sender.player() ?: return Component.empty()

        append(components.delete(messageData, viewer))
        append(components.teleport(messageData.sender.name, viewer))
        append(components.name(player))
        darkSpacer(" >> ")
        append(
            formatItemTag(
                updateLinks(highlightPlayers(messageData.message, viewer)),
                player
            )
        )
        hoverEvent(components.messageHover(messageData))
        clickSuggestsCommand("/msg ${player.name} ")
    }

    override fun formatIncomingPm(messageData: MessageData) = buildText {
        val senderName = messageData.sender.name

        darkSpacer(">> ")
        append(Component.text("PM", Colors.RED))
        darkSpacer(" | ")
        variableValue(senderName)
        darkSpacer(" -> ")
        variableValue("Dir")
        darkSpacer(" >> ")
        append(updateLinks(messageData.message))
        hoverEvent(components.messageHover(messageData))
        clickSuggestsCommand("/msg $senderName ")
    }

    override fun formatOutgoingPm(messageData: MessageData) = buildText {
        val receiverName = messageData.receiver?.name ?: "Error"

        darkSpacer(">> ")
        append(Component.text("PM", Colors.RED))
        darkSpacer(" | ")
        variableValue("Du")
        darkSpacer(" -> ")
        variableValue(receiverName)
        darkSpacer(" >> ")
        append(updateLinks(messageData.message))

        hoverEvent(components.messageHover(messageData))
        clickSuggestsCommand("/msg $receiverName ")
    }

    override fun formatTeamchat(messageData: MessageData) = buildText {
        val player = messageData.sender.player() ?: return Component.empty()

        darkSpacer(">> ")
        append(Component.text("TEAM", Colors.RED).decorate(TextDecoration.BOLD))
        darkSpacer(" | ")
        append(components.name(player))
        darkSpacer(" >> ")
        append(updateLinks(formatItemTag(messageData.message, player)))

        hoverEvent(components.messageHover(messageData))
        clickSuggestsCommand("/teamchat ")
    }

    override fun formatChannel(messageData: MessageData) = buildText {
        val player = messageData.sender.player() ?: return Component.empty()

        append(
            components.delete(
                messageData,
                messageData.receiver ?: return Component.empty()
            )
        )
        append(
            components.teleport(
                messageData.sender.name,
                messageData.receiver ?: return Component.empty()
            )
        )
        append(components.channel(messageData.channel?.channelName ?: "Unbekannter Kanal"))
        append(components.name(player))
        darkSpacer(" >> ")
        append(updateLinks(formatItemTag(messageData.message, player)))
        hoverEvent(components.messageHover(messageData))
        clickSuggestsCommand("/msg ${player.name} ")
    }

    override fun formatPmSpy(messageData: MessageData) = buildText {
        val player = messageData.sender.player() ?: return Component.empty()
        val receiver = messageData.receiver ?: return Component.empty()
        val receiverPlayer = receiver.player() ?: return Component.empty()

        append(components.spyIcon())
        appendSpace()
        append(
            components.delete(
                messageData,
                messageData.receiver ?: return Component.empty()
            )
        )
        append(
            components.teleport(
                messageData.sender.name,
                messageData.receiver ?: return Component.empty()
            )
        )
        append(components.name(player))
        appendSpace()
        darkSpacer("-->")
        appendSpace()
        append(components.name(receiverPlayer))
        append(updateLinks(formatItemTag(messageData.message, player)))
        hoverEvent(components.messageHover(messageData))
        clickSuggestsCommand("/msg ${player.name} ")
    }

    override fun formatChannelSpy(messageData: MessageData) = buildText {
        val player = messageData.sender.player() ?: return Component.empty()

        append(components.spyIcon())
        appendSpace()
        append(
            components.delete(
                messageData,
                messageData.receiver ?: return Component.empty()
            )
        )
        append(
            components.teleport(
                messageData.sender.name,
                messageData.receiver ?: return Component.empty()
            )
        )
        append(components.channel(messageData.channel?.channelName ?: "Unbekannter Kanal"))
        append(components.name(player))
        darkSpacer(" >> ")
        append(updateLinks(formatItemTag(messageData.message, player)))
        hoverEvent(components.messageHover(messageData))
        clickSuggestsCommand("/msg ${player.name} ")
    }

    private val channelExceptPattern =
        Regex("^@(all|a|here|everyone)\\b\\s*", RegexOption.IGNORE_CASE)
    private val linkRegex = Regex("(?i)\\b((https?://)?[\\w-]+(\\.[\\w-]+)+(/\\S*)?)\\b")
    private val itemRegex = Regex("\\[(?i)item]")
    private val nameRegexCache = Caffeine.newBuilder()
        .expireAfterWrite(15.minutes)
        .build<String, Regex> { name ->
            Regex("(?<!\\w)@?${Regex.escape(name)}(?!\\w)")
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

    private fun highlightPlayers(rawMessage: Component, viewer: User): Component {
        var message = rawMessage

        val name = viewer.name
        val pattern = nameRegexCache.get(name)
        val viewerPlayer = viewer.player() ?: return message

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

        plugin.launch(Dispatchers.IO) {
            if (viewer.configure().pingsEnabled()) {
                viewerPlayer.playSound(sound {
                    type(Sound.BLOCK_NOTE_BLOCK_HARP)
                    source(net.kyori.adventure.sound.Sound.Source.PLAYER)
                    volume(1f)
                    pitch(2f)
                }, net.kyori.adventure.sound.Sound.Emitter.self())
            }
        }

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