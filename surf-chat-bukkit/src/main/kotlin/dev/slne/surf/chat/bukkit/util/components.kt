package dev.slne.surf.chat.bukkit.util

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.bukkit.hook.PlaceholderAPIHook
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.core.message.MessageData
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class CompletedComponents {
    fun delete(messageData: MessageData, viewer: User): Component {
        if (!viewer.hasPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_DELETE)) {
            return Component.empty()
        }

        return buildText {
            darkSpacer("[")
            error("X")
            darkSpacer("]")
            darkSpacer(" ")
            clickEvent(ClickEvent.callback {
                val signature = messageData.signature ?: run {
                    it.sendText {
                        appendPrefix()
                        error("Die Nachricht besitzt eine ungültige Signatur.")
                    }
                    return@callback
                }

                Bukkit.getServer().deleteMessage(signature)
            })
            hoverEvent(buildText {
                warning("Klicke, um die Nachricht zu löschen")
            })
        }
    }

    fun messageHover(messageData: MessageData) = buildText {
        info("Gesendet von ")
        variableValue(messageData.sender.name)
        info(" am ")
        variableValue(messageData.sentAt.unixTime())
        appendNewline()
        info("Gesendet auf Server ")
        variableValue(messageData.server)
    }

    fun name(player: Player) = buildText {
        append(
            MiniMessage.miniMessage()
                .deserialize(
                    convertLegacy(
                        PlaceholderAPIHook.parse(
                            player,
                            "%luckperms_prefix% %player_name%"
                        )
                    )
                )
        )
    }

    fun teleport(name: String, viewer: User): Component {
        if (!viewer.hasPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_TELEPORT)) {
            return Component.empty()
        }

        return buildText {
            darkSpacer("[")
            info("TP")
            darkSpacer("]")
            darkSpacer(" ")
            clickEvent(ClickEvent.runCommand("/tp $name"))
            hoverEvent(buildText {
                info("Klicke, um dich zu $name zu teleportieren")
            })
        }
    }

    fun channelTransferConfirm(target: String): Component = buildText {
        darkSpacer("[")
        success("BESTÄTIGEN")
        darkSpacer("]")
        darkSpacer(" ")
        clickEvent(ClickEvent.runCommand("/channel transfer $target confirm"))
        hoverEvent(buildText {
            success("Klicke, um die Aktion zu bestätigen")
        })
    }

    fun channel(channel: String): Component = buildText {
        darkSpacer("[")
        variableValue(channel)
        darkSpacer("]")
        darkSpacer(" ")
    }

    fun inviteAccept(channel: Channel): Component = buildText {
        darkSpacer("[")
        success("AKZEPTIEREN")
        darkSpacer("] ")
        clickEvent(ClickEvent.runCommand("/channel accept ${channel.channelName}"))
        hoverEvent(buildText {
            success("Klicke, um die Einladung zu ${channel.channelName} anzunehmen")
        })
    }

    fun inviteDecline(channel: Channel): Component = buildText {
        darkSpacer("[")
        error("ABLEHNEN")
        darkSpacer("] ")
        clickEvent(ClickEvent.runCommand("/channel decline ${channel.channelName}"))
        hoverEvent(buildText {
            error("Klicke, um die Einladung zu ${channel.channelName} abzulehnen")
        })
    }


    companion object {
        val INSTANCE = CompletedComponents()
    }
}

val components get() = CompletedComponents.INSTANCE