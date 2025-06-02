package dev.slne.surf.chat.velocity.model

import com.github.retrooper.packetevents.protocol.sound.Sounds
import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.proxy.Player
import dev.slne.surf.chat.api.model.ChatFormat
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.plugin
import dev.slne.surf.chat.velocity.util.components
import dev.slne.surf.chat.velocity.util.getPlayer
import dev.slne.surf.chat.velocity.util.toPlainText
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickOpensUrl
import dev.slne.surf.surfapi.core.api.messages.adventure.clickSuggestsCommand
import dev.slne.surf.surfapi.core.api.messages.adventure.sound
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

class VelocityChatFormat : ChatFormat {
    override fun formatMessage(
        rawMessage: Component,
        sender: ChatUser,
        viewer: ChatUser,
        messageType: MessageType,
        channel: String,
        messageID: UUID,
        warn: Boolean
    ): Component {
        val senderName = sender.getPlayer()?.username ?: return buildText {
            error("Error while formatting message: sender is not online.")
        }
        val viewerName = viewer.getPlayer()?.username ?: return buildText {
            error("Error while formatting message: viewer is not online.")
        }

        val currentServerNiceName = sender.getPlayer()?.currentServer?.getOrNull()?.serverInfo?.name ?: "Unknown"

        val viewerPlayer = viewer.getPlayer() ?: return buildText {
            error("Error while formatting message: viewer is not online.")
        }
        val senderPlayer = sender.getPlayer() ?: return buildText {
            error("Error while formatting message: sender is not online.")
        }
        return when (messageType) {
            MessageType.GLOBAL -> {
                buildText {
                    append(components.getDeleteComponent(messageID, viewerPlayer))
                    append(components.getTeleportComponent(senderName, viewerPlayer))
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LuckPermsExtension.getPrefix(senderPlayer) + " " + senderName))
                    )
                    darkSpacer(" >> ")
                    append(formatItemTag(updateLinks(highlightPlayers(rawMessage, viewerPlayer)), senderPlayer, warn))

                    hoverEvent(
                        components.getMessageHoverComponent(
                            senderName,
                            System.currentTimeMillis(),
                            currentServerNiceName
                        )
                    )
                    clickSuggestsCommand("/msg $senderName ")
                }
            }

            MessageType.CHANNEL -> {
                buildText {
                    append(components.getDeleteComponent(messageID, viewerPlayer))
                    append(components.getTeleportComponent(senderName, viewerPlayer))
                    append(components.getChannelComponent(channel))
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LuckPermsExtension.getPrefix(senderPlayer) + " " + senderName))
                    )
                    darkSpacer(" >> ")
                    append(updateLinks(formatItemTag(rawMessage, senderPlayer, warn)))

                    hoverEvent(
                        components.getMessageHoverComponent(
                            senderName,
                            System.currentTimeMillis(),
                            currentServerNiceName
                        )
                    )
                    clickSuggestsCommand("/msg $senderName ")
                }
            }

            MessageType.PRIVATE_TO -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("PM", Colors.RED))
                    darkSpacer(" | ")
                    variableValue("Du")
                    darkSpacer(" -> ")
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LuckPermsExtension.getPrefix(viewerPlayer) + " " + viewerName))
                    )
                    darkSpacer(" >> ")
                    append(updateLinks(formatItemTag(rawMessage, senderPlayer, warn)))

                    hoverEvent(
                        components.getMessageHoverComponent(
                            senderName,
                            System.currentTimeMillis(),
                            currentServerNiceName
                        )
                    )
                    clickSuggestsCommand("/msg $senderName ")
                }
            }

            MessageType.PRIVATE_FROM -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("PM", Colors.RED))
                    darkSpacer(" | ")
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LuckPermsExtension.getPrefix(senderPlayer) + " " + senderName))
                    )
                    darkSpacer(" -> ")
                    variableValue("Dir")
                    darkSpacer(" >> ")
                    append(updateLinks(formatItemTag(rawMessage, senderPlayer, warn)))

                    hoverEvent(
                        components.getMessageHoverComponent(
                            senderName,
                            System.currentTimeMillis(),
                            currentServerNiceName
                        )
                    )
                    clickSuggestsCommand("/msg $senderName ")
                }
            }

            MessageType.TEAM -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("TEAM", Colors.RED).decorate(TextDecoration.BOLD))
                    darkSpacer(" | ")
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LuckPermsExtension.getPrefix(senderPlayer) + " " + senderName))
                    )
                    darkSpacer(" >> ")
                    append(updateLinks(formatItemTag(rawMessage, senderPlayer, warn)))

                    hoverEvent(
                        components.getMessageHoverComponent(
                            senderName,
                            System.currentTimeMillis(),
                            currentServerNiceName
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
                            .deserialize(convertLegacy(LuckPermsExtension.getPrefix(senderPlayer) + " " + senderName))
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
                    clickSuggestsCommand("/msg $senderName ")
                }
            }
        }
    }

    private fun formatItemTag(rawMessage: Component, player: Player, warn: Boolean): Component {
        return rawMessage
    }

    private fun highlightPlayers(rawMessage: Component, player: Player): Component {
        var message = rawMessage

        for (onlinePlayer in plugin.proxy.allPlayers) {
            val name = onlinePlayer.username
            val pattern = Regex("(?<!\\w)@?$name(?!\\w)")

            if (!pattern.containsMatchIn(message.toPlainText())) {
                continue
            }

            if(onlinePlayer == player) {
                container.launch {
                    val user = databaseService.getUser(onlinePlayer.uniqueId)

                    if (user.settings.pingsEnabled) {
                        onlinePlayer.playSound(sound {
                            type(Key.key(Sounds.BLOCK_NOTE_BLOCK_PLING.soundId.toString()))
                            source(Sound.Source.PLAYER)
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

        return message
    }


    fun convertLegacy(input: String): String {
        val regex = Regex("&#[A-Fa-f0-9]{6}")
        return regex.replace(input) { matchResult ->
            val hex = matchResult.value.removePrefix("&#")
            "<#$hex>"
        }
    }

    private fun updateLinks(rawMessage: Component): Component {
        val pattern = Regex("(?i)\\b((https?://)?[\\w-]+(\\.[\\w-]+)+(/\\S*)?)\\b")

        var message = rawMessage
        val text = rawMessage.toPlainText()

        for (match in pattern.findAll(text)) {
            val url = match.value

            if (!message.toPlainText().contains(url)) continue

            message = message.replaceText(
                TextReplacementConfig.builder()
                    .match(Regex.escape(url))
                    .replacement(
                        buildText {
                            text(url)
                            hoverEvent (buildText {
                                info("Klicke hier, um den Link zu öffnen.")
                            })
                                .clickOpensUrl(url)
                        }
                    )
                    .build()
            )
        }

        return message
    }
}

val velocityChatFormat get() = VelocityChatFormat()