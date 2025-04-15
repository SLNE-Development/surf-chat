package dev.slne.surf.chat.bukkit.model

import dev.slne.surf.chat.api.model.ChatFormatModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.bukkit.extension.LuckPermsExtension
import dev.slne.surf.chat.bukkit.util.components
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.UUID
import java.util.regex.Pattern

class BukkitChatFormat: ChatFormatModel {
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
                    append(rawMessage)
                }.parseItemPlaceholder(sender, warn)
            }

            ChatMessageType.CHANNEL -> {
                buildText {
                    append(MiniMessage.miniMessage().deserialize(LuckPermsExtension.getPrefix(sender) + sender.name))
                    darkSpacer(" >> ")
                    append(components.getChannelComponent(channel))
                    append(rawMessage)
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
        val stack = player.inventory.itemInMainHand

        if (stack.type == Material.AIR) {
            surfChatApi.sendText(player, buildText {
                error("Du hast kein Item in der Hand!")
            })
            return this
        }

        return this.replaceText(TextReplacementConfig.builder()
            .matchLiteral("[item]")
            .replacement(when {
                stack.amount > 1 -> buildText {
                    variableValue("${stack.amount}x ")
                    text(stack.displayName(), Colors.VARIABLE_VALUE)
                }
                else -> text(stack.displayName(), Colors.VARIABLE_VALUE)
            })
            .build()
        )
    }


    /**
     *
     * This method is currently not implemented.
     * It has to be fixed
     * With many players online, it could produce some performance issues.
     *
     */
    private fun highlightPlayers(rawMessage: Component): Component {
        var message = rawMessage

        for (onlinePlayer in Bukkit.getOnlinePlayers()) {
            if(!message.contains(Component.text(onlinePlayer.name))) {
                continue
            }

            message = message.replaceText(TextReplacementConfig
                .builder()
                .match(Pattern.quote(onlinePlayer.name))
                .replacement(buildText {
                    variableValue(onlinePlayer.name)
                })
                .build())
        }

        return message
    }
}