package dev.slne.surf.chat.bukkit.util

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.bukkit.util.utils.formatTime
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.entity.Player
import java.util.*

class CompletedComponents {
    fun getDeleteComponent(messageID: UUID, viewer: Player): Component {
        if (!viewer.hasPermission(ChatPermissionRegistry.COMMAND_SURFCHAT_DELETE)) {
            return Component.empty()
        }

        return buildText {
            darkSpacer("[")
            error("X")
            darkSpacer("]")
            darkSpacer(" ")
            clickEvent(ClickEvent.runCommand("/surfchat delete $messageID"))
            hoverEvent(buildText {
                warning("Klicke, um die Nachricht zu löschen")
            })
        }
    }

    fun getTeleportComponent(name: String, viewer: Player): Component {
        if (!viewer.hasPermission(ChatPermissionRegistry.COMMAND_SURFCHAT_TELEPORT)) {
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

    fun getTransferConfirmComponent(target: String): Component = buildText {
        darkSpacer("[")
        success("BESTÄTIGEN")
        darkSpacer("]")
        darkSpacer(" ")
        clickEvent(ClickEvent.runCommand("/channel transferOwnership $target confirm"))
        hoverEvent(buildText {
            success("Klicke, um die Aktion zu bestätigen")
        })
    }

    fun getChannelComponent(channel: String): Component = buildText {
        darkSpacer("[")
        variableValue(channel)
        darkSpacer("]")
        darkSpacer(" ")
    }

    fun getInviteAcceptComponent(channel: Channel): Component = buildText {
        darkSpacer("[")
        success("AKZEPTIEREN")
        darkSpacer("] ")
        clickEvent(ClickEvent.runCommand("/channel accept ${channel.name}"))
        hoverEvent(buildText {
            success("Klicke, um die Einladung zu ${channel.name} anzunehmen")
        })
    }

    fun getInviteDeclineComponent(channel: Channel): Component = buildText {
        darkSpacer("[")
        error("ABLEHNEN")
        darkSpacer("] ")
        clickEvent(ClickEvent.runCommand("/channel decline ${channel.name}"))
        hoverEvent(buildText {
            error("Klicke, um die Einladung zu ${channel.name} abzulehnen")
        })
    }

    fun getMessageHoverComponent(sender: String, time: Long): Component =
        buildText {
            variableKey("Gesendet von: ")
            variableValue(sender)
            appendNewline()
            variableKey("Gesendet am: ")
            variableValue(formatTime(time))
            appendNewline()
            variableKey("Gesendet auf: ")
            variableValue("/")
        }

    fun getIgnoreListHoverComponent(user: String): Component =
        buildText {
            info("Klicke, um die Blockierung des Spielers ")
            variableValue(user)
            info(" aufzuheben.")
        }


    companion object {
        val INSTANCE = CompletedComponents()
    }
}

val components get() = CompletedComponents.INSTANCE