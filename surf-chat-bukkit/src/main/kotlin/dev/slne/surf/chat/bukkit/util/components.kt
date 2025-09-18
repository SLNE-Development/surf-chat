package dev.slne.surf.chat.bukkit.util

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.bukkit.hook.MiniPlaceholdersHook
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.message.MessageData
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun SurfComponentBuilder.appendDelete(messageData: MessageData) = append(buildText {
    darkSpacer("[")
    error("X")
    darkSpacer("]")
    darkSpacer(" ")
    clickEvent(ClickEvent.callback {
        val signature = messageData.signedMessage?.signature() ?: run {
            it.sendText {
                appendPrefix()
                error("Die Nachricht besitzt eine ungültige Signatur und konnte nicht gelöscht werden.")
            }
            return@callback
        }

        Bukkit.getServer().deleteMessage(signature)
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

        plugin.launch {
            historyService.markDeleted(messageData.messageUuid, it.name())
        }
    })
    hoverEvent(buildText {
        warning("Klicke, um die Nachricht zu löschen")
    })
})

fun SurfComponentBuilder.appendMessageData(messageData: MessageData) = append(buildText {
    info("Gesendet von ")
    variableValue(messageData.sender.name)
    info(" am ")
    variableValue(messageData.sentAt.unixTime())
    appendNewline()
    info("Gesendet auf Server ")
    variableValue(messageData.server.name)
})

fun SurfComponentBuilder.appendName(player: Player) = append {
    append(
        MiniPlaceholdersHook.parseAudience(player, "%luckperms_prefix% %player_name%")
    )
}

fun SurfComponentBuilder.appendTeleport(name: String, viewer: User) = append {
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