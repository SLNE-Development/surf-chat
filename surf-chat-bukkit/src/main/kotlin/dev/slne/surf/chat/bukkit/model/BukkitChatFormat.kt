package dev.slne.surf.chat.bukkit.model

import dev.slne.surf.chat.api.model.ChatFormatModel
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.user.DisplayUser
import dev.slne.surf.chat.bukkit.util.components
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import java.util.UUID

class BukkitChatFormat: ChatFormatModel {
    override fun formatMessage(
        rawMessage: Component,
        sender: DisplayUser,
        viewer: DisplayUser,
        messageType: ChatMessageType,
        channel: String,
        messageID: UUID
    ): Component {
        return when(messageType) {
            ChatMessageType.GLOBAL -> {
                buildText {
                    append(components.getDeleteComponent(messageID))
                    append(components.getTeleportComponent(sender.name))
                    primary(sender.name)
                    darkSpacer(" >> ")
                    append(rawMessage)
                }
            }

            ChatMessageType.CHANNEL -> {
                buildText {
                    primary(sender.name)
                    darkSpacer(" >> ")
                    append(components.getChannelComponent(channel))
                    append(rawMessage)
                }
            }

            ChatMessageType.PRIVATE_TO -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("PM", Colors.RED))
                    darkSpacer(" | ")
                    variableValue("Du")
                    darkSpacer(" -> ")
                    variableValue(viewer.name)
                    darkSpacer(" >> ")
                    append(rawMessage)
                }
            }

            ChatMessageType.PRIVATE_FROM -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("PM", Colors.RED))
                    darkSpacer(" | ")
                    variableValue(sender.name)
                    darkSpacer(" -> ")
                    variableValue("Dir")
                    darkSpacer(" >> ")
                    append(rawMessage)
                }
            }

            ChatMessageType.TEAM -> {
                buildText {
                    darkSpacer(">> ")
                    append(Component.text("TEAM", Colors.RED).decorate(TextDecoration.BOLD))
                    darkSpacer(" | ")
                    variableValue(sender.name)
                    darkSpacer(" >> ")
                    append(rawMessage)
                }
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
                    variableValue(sender.name)
                    darkSpacer(" >> ")
                    append(rawMessage)
                }
            }
        }
    }
}