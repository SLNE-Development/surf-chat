package dev.slne.surf.chat.paper.util

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.common.message.MessageData
import dev.slne.surf.chat.core.common.netty.packet.serverbound.ServerboundMessageDeletePacket
import dev.slne.surf.chat.core.common.netty.packet.serverbound.history.ServerboundHistoryMarkDeletedPacket
import dev.slne.surf.chat.paper.hook.LuckPermsHook
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.cloud.api.client.netty.packet.fireAndForget
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun SurfComponentBuilder.appendDelete(messageData: MessageData) = append(buildText {
    darkSpacer("[")
    error("X")
    darkSpacer("]")
    darkSpacer(" ")
    clickEvent(ClickEvent.callback {
        val signature = messageData.signature ?: run {
            it.sendText {
                appendPrefix()
                error("Die Nachricht besitzt eine ungültige Signatur und konnte nicht gelöscht werden.")
            }
            return@callback
        }

        ServerboundMessageDeletePacket(signature).fireAndForget()
        Bukkit.getOnlinePlayers()
            .filter { online -> online.hasPermission(SurfChatPermissionRegistry.TEAM_NOTIFY_DELETION) }
            .forEach { online ->
                online.sendText {
                    appendPrefix()
                    variableValue(it.name())
                    info(" hat eine Nachricht von ")
                    variableValue(messageData.sender.name)
                    info(" gelöscht: ")
                    append(messageData.message)
                }
            }

        ServerboundHistoryMarkDeletedPacket(messageData.messageUuid, it.name()).fireAndForget()
    })
    hoverEvent(buildText {
        warning("Klicke, um die Nachricht zu löschen")
    })
})

fun SurfComponentBuilder.appendName(player: Player) = append {
    append(
        MiniMessage.miniMessage()
            .deserialize(convertLegacy("${LuckPermsHook.getPrefix(player.uniqueId)}${player.name}"))
    )
}

fun SurfComponentBuilder.appendTeleport(name: String, viewer: Player) = append {
    darkSpacer("[")
    info("TP")
    darkSpacer("]")
    darkSpacer(" ")
    clickEvent(ClickEvent.runCommand("/teleport ${viewer.name} $name"))
    hoverEvent(buildText {
        info("Klicke, um dich zu $name zu teleportieren")
    })
}

fun SurfComponentBuilder.appendConfirm(execution: String) = append {
    darkSpacer("[")
    success("BESTÄTIGEN")
    darkSpacer("]")
    darkSpacer(" ")
    clickEvent(ClickEvent.runCommand(execution))
    hoverEvent(buildText {
        success("Klicke, um die Aktion zu bestätigen")
    })
}

fun SurfComponentBuilder.appendChannelPrefix(channelName: String) = append {
    darkSpacer("[")
    variableValue(channelName)
    darkSpacer("]")
    darkSpacer(" ")
}

fun SurfComponentBuilder.appendInviteAccept(channel: Channel) = append {
    darkSpacer("[")
    success("AKZEPTIEREN")
    darkSpacer("] ")
    clickEvent(ClickEvent.runCommand("/channel accept ${channel.channelName}"))
    hoverEvent(buildText {
        success("Klicke, um die Einladung zu ${channel.channelName} anzunehmen")
    })
}

fun SurfComponentBuilder.appendInviteDecline(channel: Channel) = append {
    darkSpacer("[")
    error("ABLEHNEN")
    darkSpacer("] ")
    clickEvent(ClickEvent.runCommand("/channel decline ${channel.channelName}"))
    hoverEvent(buildText {
        error("Klicke, um die Einladung zu ${channel.channelName} abzulehnen")
    })
}

fun SurfComponentBuilder.appendSpyIcon() = append {
    spacer("[")
    info("SPY")
    spacer("]")
}

fun SurfComponentBuilder.appendWarningPrefix() = append {
    darkSpacer("[")
    error("!", TextDecoration.BOLD)
    darkSpacer("]")
    appendSpace()
}

fun SurfComponentBuilder.appendBotIcon() = append {
    darkSpacer("[")
    info("ARTY".toSmallCaps())
    darkSpacer("]")
    appendSpace()
}

fun SurfComponentBuilder.appendStatusIcon(status: Boolean) = append {
    darkSpacer("[")
    if (status) {
        text("✔", Colors.GREEN)
    } else {
        text("✘", Colors.RED)
    }
    darkSpacer("]")
    appendSpace()
}