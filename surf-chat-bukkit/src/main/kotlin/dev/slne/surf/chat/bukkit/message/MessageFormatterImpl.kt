package dev.slne.surf.chat.bukkit.message

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.components
import dev.slne.surf.chat.bukkit.util.plainText
import dev.slne.surf.chat.bukkit.util.player
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.message.MessageData
import dev.slne.surf.chat.core.message.MessageFormatter
import dev.slne.surf.surfapi.core.api.messages.adventure.*
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
        val viewerPlayer = viewer.player() ?: return Component.empty()
        val player = messageData.sender.player() ?: return Component.empty()

        append(components.delete(messageData.messageId, viewer))
        append(components.teleport(messageData.sender.name, viewer))
        append(components.name(player))
        darkSpacer(" >> ")
        append(
            formatItemTag(
                updateLinks(highlightPlayers(messageData.message, viewerPlayer)),
                player
            )
        )
        hoverEvent(
            components.messageHover(messageData)
        )
        clickSuggestsCommand("/msg ${player.name} ")
    }

    private val channelExceptPattern =
        Regex("^@(all|a|here|everyone)\\b\\s*", RegexOption.IGNORE_CASE)
    private val linkRegex = Regex("(?i)\\b((https?://)?[\\w-]+(\\.[\\w-]+)+(/\\S*)?)\\b")
    private val itemRegex = Regex("\\[(?i)item]")
    private val nameRegexCache = Caffeine.newBuilder()
        .expireAfterWrite(15.minutes)
        .build<String, Regex> {
            Regex("(?<!\\w)@?$it(?!\\w)")
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

    private fun highlightPlayers(rawMessage: Component, viewer: Player): Component {
        var message = rawMessage

        val pattern = nameRegexCache.get(viewer.name)
        val user = viewer.user() ?: return message

        if (!pattern.containsMatchIn(message.plainText())) {
            return message
        }

        plugin.launch(plugin.entityDispatcher(viewer)) {
            if (user.configure().pingsEnabled()) {
                viewer.playSound(sound {
                    type(Sound.BLOCK_NOTE_BLOCK_PLING)
                    source(net.kyori.adventure.sound.Sound.Source.PLAYER)
                    volume(0.25f)
                    pitch(2f)
                })
            }

            message = message.replaceText(
                TextReplacementConfig
                    .builder()
                    .match(pattern.pattern)
                    .replacement(buildText {
                        append(Component.text(viewer.name))
                        decorate(TextDecoration.BOLD)
                    })
                    .build()
            )
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