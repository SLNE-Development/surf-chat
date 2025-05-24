package dev.slne.surf.chat.bukkit.model

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.model.ChatFormatModel
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.extension.LuckPermsExtension
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.components
import dev.slne.surf.chat.bukkit.util.utils.pluginConfig
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.bukkit.util.utils.serverPlayers
import dev.slne.surf.chat.bukkit.util.utils.toPlainText
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickOpensUrl
import dev.slne.surf.surfapi.core.api.messages.adventure.sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import java.util.*

class BukkitChatFormat : ChatFormatModel {
    private var currentServerNiceName: String = "Unknown"

    override fun formatMessage(
        rawMessage: Component,
        sender: Player,
        viewer: Player,
        messageType: ChatMessageType,
        channel: String,
        messageID: UUID,
        warn: Boolean
    ): Component {
        return when (messageType) {
            ChatMessageType.GLOBAL -> {
                buildText {
                    append(components.getDeleteComponent(messageID, viewer))
                    append(components.getTeleportComponent(sender.name, viewer))
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LuckPermsExtension.getPrefix(sender) + " " + sender.name))
                    )
                    darkSpacer(" >> ")
                    append(formatItemTag(updateLinks(highlightPlayers(rawMessage, viewer)), sender, warn))

                    hoverEvent(
                        components.getMessageHoverComponent(
                            sender.name,
                            System.currentTimeMillis(),
                            currentServerNiceName
                        )
                    )
                    clickEvent(ClickEvent.suggestCommand("/msg ${sender.name} "))
                }
            }

            ChatMessageType.CHANNEL -> {
                buildText {
                    append(components.getDeleteComponent(messageID, viewer))
                    append(components.getTeleportComponent(sender.name, viewer))
                    append(components.getChannelComponent(channel))
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LuckPermsExtension.getPrefix(sender) + " " + sender.name))
                    )
                    darkSpacer(" >> ")
                    append(updateLinks(formatItemTag(rawMessage, sender, warn)))

                    hoverEvent(
                        components.getMessageHoverComponent(
                            sender.name,
                            System.currentTimeMillis(),
                            currentServerNiceName
                        )
                    )
                    clickEvent(ClickEvent.suggestCommand("/msg ${sender.name} "))
                }
            }

            ChatMessageType.PRIVATE_TO -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("PM", Colors.RED))
                    darkSpacer(" | ")
                    variableValue("Du")
                    darkSpacer(" -> ")
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LuckPermsExtension.getPrefix(viewer) + " " + viewer.name))
                    )
                    darkSpacer(" >> ")
                    append(updateLinks(formatItemTag(rawMessage, sender, warn)))

                    hoverEvent(
                        components.getMessageHoverComponent(
                            sender.name,
                            System.currentTimeMillis(),
                            currentServerNiceName
                        )
                    )
                    clickEvent(ClickEvent.suggestCommand("/msg ${viewer.name} "))
                }
            }

            ChatMessageType.PRIVATE_FROM -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("PM", Colors.RED))
                    darkSpacer(" | ")
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LuckPermsExtension.getPrefix(sender) + " " + sender.name))
                    )
                    darkSpacer(" -> ")
                    variableValue("Dir")
                    darkSpacer(" >> ")
                    append(updateLinks(formatItemTag(rawMessage, sender, warn)))

                    hoverEvent(
                        components.getMessageHoverComponent(
                            sender.name,
                            System.currentTimeMillis(),
                            currentServerNiceName
                        )
                    )
                    clickEvent(ClickEvent.suggestCommand("/msg ${sender.name} "))
                }
            }

            ChatMessageType.TEAM -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("TEAM", Colors.RED).decorate(TextDecoration.BOLD))
                    darkSpacer(" | ")
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LuckPermsExtension.getPrefix(sender) + " " + sender.name))
                    )
                    darkSpacer(" >> ")
                    append(updateLinks(formatItemTag(rawMessage, sender, warn)))

                    hoverEvent(
                        components.getMessageHoverComponent(
                            sender.name,
                            System.currentTimeMillis(),
                            currentServerNiceName
                        )
                    )
                    clickEvent(ClickEvent.suggestCommand("/msg ${sender.name} "))
                }
            }

            /**
             * This is a special case for private messages,
             * no message should be sent with this type.
             */

            ChatMessageType.PRIVATE -> {
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
            ChatMessageType.INTERNAL -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("INTERNAL", Colors.DARK_RED))
                    darkSpacer(" | ")
                    append(
                        MiniMessage.miniMessage()
                            .deserialize(convertLegacy(LuckPermsExtension.getPrefix(sender) + " " + sender.name))
                    )
                    darkSpacer(" >> ")
                    append(rawMessage)
                }
            }

            /**
             * This is a special case for private messages,
             * no message should be sent with this type.
             */
            ChatMessageType.REPLY -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("Antwort", Colors.RED))
                    darkSpacer(" | ")
                    variableValue("Dir ")
                    darkSpacer(" >> ")
                    append(rawMessage)
                    clickEvent(ClickEvent.suggestCommand("/msg ${sender.name} "))
                }
            }
        }
    }

    override fun loadServer() {
        this.currentServerNiceName =
            pluginConfig.getString("cross-server-messages.current-server-name") ?: "Unknown"
    }

    override fun getServer(): String {
        return currentServerNiceName
    }

    private fun formatItemTag(rawMessage: Component, player: Player, warn: Boolean): Component {
        var message = rawMessage
        val item = player.inventory.itemInMainHand
        val pattern = Regex("\\[(?i)item]")

        if (!pattern.containsMatchIn(message.toPlainText())) {
            return rawMessage
        }

        if (item.type == Material.AIR) {
            if (warn) {
                player.sendPrefixed {
                    error("Du hast kein Item in der Hand!")
                }
            }
            return rawMessage
        }

        message = message.replaceText(
            TextReplacementConfig
                .builder()
                .match(pattern.pattern)
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

        for (onlinePlayer in serverPlayers) {
            val name = onlinePlayer.name
            val pattern = Regex("(?<!\\w)@?$name(?!\\w)")

            if (!pattern.containsMatchIn(message.toPlainText())) {
                continue
            }

            if(onlinePlayer == player) {
                plugin.launch {
                    val user = databaseService.getUser(onlinePlayer.uniqueId)

                    if (user.soundEnabled) {
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
        val pattern = Regex("(?i)\\b(https?://[\\w-]+(\\.[\\w-]+)+(/\\S*)?)\\b")

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
                                    info("Klicke hier, um den Link zu öffnen!")
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