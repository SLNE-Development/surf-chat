package dev.slne.surf.chat.bukkit.model

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.model.ChatFormat
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.bukkit.extension.LPHook
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.components
import dev.slne.surf.chat.bukkit.util.plain
import dev.slne.surf.chat.bukkit.util.toPlayer
import dev.slne.surf.chat.bukkit.util.utils.serverPlayers
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickOpensUrl
import dev.slne.surf.surfapi.core.api.messages.adventure.clickSuggestsCommand
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.sound
import kotlinx.coroutines.withContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

class BukkitChatFormat : ChatFormat {
    private val nameRegexCache = Caffeine.newBuilder()
        .expireAfterWrite(15.minutes.toJavaDuration())
        .build<String, Regex> {
            Regex("(?<!\\w)@?$it(?!\\w)")
        }

    val hexRegex = Regex("&#[A-Fa-f0-9]{6}")
    val linkRegex = Regex("(?i)\\b((https?://)?[\\w-]+(\\.[\\w-]+)+(/\\S*)?)\\b")
    val itemRegex = Regex("\\[(?i)item]")

    override fun format(
        rawMessage: Component,
        sender: ChatUser,
        viewer: ChatUser,
        messageType: MessageType,
        channel: String,
        messageID: UUID,
        warn: Boolean
    ): Component {
        val viewerPlayer = viewer.toPlayer() ?: return Component.empty()
        val senderPlayer = sender.toPlayer() ?: return Component.empty()

        return when (messageType) {
            MessageType.GLOBAL -> {
                buildText {
                    append(components.getDeleteComponent(messageID, viewerPlayer))
                    append(components.getTeleportComponent(sender.name, viewerPlayer))
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LPHook.findPrefix(senderPlayer) + " " + sender.name))
                    )
                    darkSpacer(" >> ")
                    append(formatItemTag(updateLinks(highlightPlayers(rawMessage, viewerPlayer)), senderPlayer, warn))

                    hoverEvent(
                        components.getMessageHoverComponent(
                            sender.name,
                            System.currentTimeMillis()
                        )
                    )
                    clickSuggestsCommand("/msg ${sender.name} ")
                }
            }

            MessageType.CHANNEL -> {
                buildText {
                    append(components.getDeleteComponent(messageID, viewerPlayer))
                    append(components.getTeleportComponent(sender.name, viewerPlayer))
                    append(components.getChannelComponent(channel))
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LPHook.findPrefix(senderPlayer) + " " + sender.name))
                    )
                    darkSpacer(" >> ")
                    append(updateLinks(formatItemTag(rawMessage, senderPlayer, warn)))

                    hoverEvent(
                        components.getMessageHoverComponent(
                            sender.name,
                            System.currentTimeMillis()
                        )
                    )
                    clickSuggestsCommand("/msg ${sender.name} ")
                }
            }

            MessageType.PRIVATE_TO -> {
                buildText {
                    darkSpacer(">> ")
                    error("PM")
                    darkSpacer(" | ")
                    variableValue("Du")
                    darkSpacer(" -> ")
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LPHook.findPrefix(viewerPlayer) + " " + viewer.name))
                    )
                    darkSpacer(" >> ")
                    append(updateLinks(formatItemTag(rawMessage, senderPlayer, warn)))

                    hoverEvent(
                        components.getMessageHoverComponent(
                            sender.name,
                            System.currentTimeMillis()
                        )
                    )
                    clickSuggestsCommand("/msg ${viewer.name} ")
                }
            }

            MessageType.PRIVATE_FROM -> {
                buildText {
                    darkSpacer(">> ")
                    error("PM")
                    darkSpacer(" | ")
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LPHook.findPrefix(senderPlayer) + " " + sender.name))
                    )
                    darkSpacer(" -> ")
                    variableValue("Dir")
                    darkSpacer(" >> ")
                    append(updateLinks(formatItemTag(rawMessage, senderPlayer, warn)))

                    hoverEvent(
                        components.getMessageHoverComponent(
                            sender.name,
                            System.currentTimeMillis()
                        )
                    )
                    clickSuggestsCommand("/msg ${sender.name} ")
                }
            }

            MessageType.TEAM -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("TEAM", Colors.RED).decorate(TextDecoration.BOLD))
                    darkSpacer(" | ")
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LPHook.findPrefix(senderPlayer) + " " + sender.name))
                    )
                    darkSpacer(" >> ")
                    append(updateLinks(formatItemTag(rawMessage, senderPlayer, warn)))

                    hoverEvent(
                        components.getMessageHoverComponent(
                            sender.name,
                            System.currentTimeMillis()
                        )
                    )
                    clickSuggestsCommand("/teamchat ")
                }
            }

            /**
             * This is a special case for private messages,
             * no message should be sent with this type.
             */

            MessageType.PRIVATE -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("PM", Colors.RED))
                    darkSpacer(" | ")
                    variableValue("Dir ")
                    darkSpacer(" >> ")
                    append(rawMessage)
                }
            }

            /**
             * This is a special case for internal messages,
             * which are not sent to players but are logged
             */
            MessageType.INTERNAL -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("INTERNAL", Colors.DARK_RED))
                    darkSpacer(" | ")
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LPHook.findPrefix(senderPlayer) + " " + sender.name))
                    )
                    darkSpacer(" >> ")
                    append(rawMessage)
                }
            }

            /**
             * This is a special case for private messages,
             * no message should be sent with this type.
             */
            MessageType.REPLY -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("Antwort", Colors.RED))
                    darkSpacer(" | ")
                    variableValue("Dir ")
                    darkSpacer(" >> ")
                    append(rawMessage)
                    clickSuggestsCommand("/msg ${sender.name} ")
                }
            }
        }
    }

    private fun formatItemTag(rawMessage: Component, player: Player, warn: Boolean): Component {
        var message = rawMessage
        val item = player.inventory.itemInMainHand

        if (!itemRegex.containsMatchIn(message.plain())) {
            return rawMessage
        }

        if (item.type == Material.AIR) {
            if (warn) {
                player.sendText {
                    appendPrefix()
                    error("Du hast kein Item in der Hand!")
                }
            }
            return rawMessage
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

    private fun highlightPlayers(rawMessage: Component, player: Player): Component {
        var message = rawMessage

        plugin.launch {
            for (onlinePlayer in serverPlayers) {
                val name = onlinePlayer.name
                val pattern = nameRegexCache.get(name)

                if (!pattern.containsMatchIn(message.plain())) {
                    continue
                }

                if(onlinePlayer == player) {
                    withContext(plugin.entityDispatcher(player)) {
                        val user = databaseService.getUser(onlinePlayer.uniqueId)

                        if (user.settings.pingsEnabled) {
                            onlinePlayer.playSound(sound {
                                type(Sound.BLOCK_NOTE_BLOCK_PLING)
                                source(net.kyori.adventure.sound.Sound.Source.PLAYER)
                                volume(0.25f)
                                pitch(2f)
                            })
                        }
                    }
                }

                message = message.replaceText(
                    TextReplacementConfig
                        .builder()
                        .match(pattern.pattern)
                        .replacement(buildText {
                            append(Component.text(name))
                            decorate(TextDecoration.BOLD)
                        })
                        .build()
                )
            }
        }

        return message
    }


    fun convertLegacy(input: String): String {
        return hexRegex.replace(input) { matchResult ->
            val hex = matchResult.value.removePrefix("&#")
            "<#$hex>"
        }
    }

    private fun updateLinks(rawMessage: Component): Component {
        var message = rawMessage
        val text = rawMessage.plain()

        for (match in linkRegex.findAll(text)) {
            val url = match.value

            if (!text.contains(url)) {
                continue
            }

            message = message.replaceText(
                TextReplacementConfig.builder()
                    .match(Regex.escape(url))
                    .replacement(
                        buildText {
                            text(url)
                            hoverEvent (buildText {
                                info("Klicke hier, um den Link zu öffnen.")
                            })
                            clickOpensUrl(url)
                        }
                    )
                    .build()
            )
        }

        return message
    }
}