package dev.slne.surf.chat.bukkit.model

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.model.ChatFormatModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.extension.LuckPermsExtension
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.components
import dev.slne.surf.chat.bukkit.util.pluginConfig
import dev.slne.surf.chat.bukkit.util.serverPlayers
import dev.slne.surf.chat.bukkit.util.toPlainText
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sound

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player

import java.util.UUID

class BukkitChatFormat: ChatFormatModel {
    private var currentServerNiceName: String = "Unknown"

    init {
        currentServerNiceName = pluginConfig.getString("cross-server-messages.current-server-name") ?: "Unknown"
    }

    override fun formatMessage (
        rawMessage: Component,
        sender: Player,
        viewer: Player,
        messageType: ChatMessageType,
        channel: String,
        messageID: UUID,
        warn: Boolean
    ): Component {
        return when(messageType) {
            ChatMessageType.GLOBAL -> {
                buildText {
                    append(components.getDeleteComponent(messageID, viewer))
                    append(components.getTeleportComponent(sender.name, viewer))
                    append(MiniMessage.miniMessage().deserialize(LuckPermsExtension.getPrefix(sender) + sender.name))
                    darkSpacer(" >> ")
                    append(highlightPlayers(rawMessage))
                }.parseItemPlaceholder(sender, warn)
            }

            ChatMessageType.CHANNEL -> {
                buildText {
                    append(MiniMessage.miniMessage().deserialize(LuckPermsExtension.getPrefix(sender) + sender.name))
                    darkSpacer(" >> ")
                    append(components.getChannelComponent(channel))
                    append(highlightPlayers(rawMessage))
                }.parseItemPlaceholder(sender, warn)
            }

            ChatMessageType.PRIVATE_TO -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("PM", Colors.RED))
                    darkSpacer(" | ")
                    variableValue("Du")
                    darkSpacer(" -> ")
                    append(MiniMessage.miniMessage().deserialize(LuckPermsExtension.getPrefix(viewer) + viewer.name))
                    darkSpacer(" >> ")
                    append(rawMessage)
                }.parseItemPlaceholder(sender, warn)
            }

            ChatMessageType.PRIVATE_FROM -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("PM", Colors.RED))
                    darkSpacer(" | ")
                    append(MiniMessage.miniMessage().deserialize(LuckPermsExtension.getPrefix(sender) + sender.name))
                    darkSpacer(" -> ")
                    variableValue("Dir")
                    darkSpacer(" >> ")
                    append(rawMessage)
                }.parseItemPlaceholder(sender, warn)
            }

            ChatMessageType.TEAM -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("TEAM", Colors.RED).decorate(TextDecoration.BOLD))
                    darkSpacer(" | ")
                    append(MiniMessage.miniMessage().deserialize(LuckPermsExtension.getPrefix(sender) + sender.name))
                    darkSpacer(" >> ")
                    append(highlightPlayers(rawMessage))
                }.parseItemPlaceholder(sender, warn)
            }

            /**
             * This is a special case for private messages,
             * no message should be sent with this type.
             */

            ChatMessageType.PRIVATE_GENERAL -> {
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
                    append(MiniMessage.miniMessage().deserialize(LuckPermsExtension.getPrefix(sender) + sender.name))
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
                }
            }
        }
    }

    private fun Component.parseItemPlaceholder(player: Player, warn: Boolean): Component {
        return this
    }

    //.replaceText(TextReplacementConfig.builder()
    //            .matchLiteral("[item]")
    //            .replacement(player.getItemComponent(warn))
    //            .build()
    //        )

    private fun Player.getItemComponent(warn: Boolean): Component = buildText {
        val stack = inventory.itemInMainHand

        if (stack.type == Material.AIR) {
            if(warn) {
                surfChatApi.sendText(this@getItemComponent, buildText {
                    error("Du hast kein Item in der Hand!")
                })
            }
            return@buildText
        }

        if (stack.amount > 1) {
            variableValue("${stack.amount}x ")
        }

        append(variableValue(stack.displayName().toPlainText()))
        hoverEvent(stack.asHoverEvent())
    }

    private fun highlightPlayers(rawMessage: Component): Component {
        var message = rawMessage

        for (onlinePlayer in serverPlayers) {
            val name = onlinePlayer.name
            val pattern = Regex("(?<!\\w)@?$name(?!\\w)")

            if (!pattern.containsMatchIn(message.toPlainText())) continue

            plugin.launch {
                val user = databaseService.getUser(onlinePlayer.uniqueId)

                if(user.likesSound) {
                    onlinePlayer.playSound(sound {
                        type(Sound.BLOCK_NOTE_BLOCK_PLING)
                        source(net.kyori.adventure.sound.Sound.Source.PLAYER)
                        volume(0.25f)
                        pitch(2f)
                    })
                }
            }



            message = message.replaceText(TextReplacementConfig
                .builder()
                .match(pattern.pattern)
                .replacement(buildText {
                    append(Component.text(name))
                    decorate(TextDecoration.BOLD)
                })
                .build())
        }

        return message
    }

}