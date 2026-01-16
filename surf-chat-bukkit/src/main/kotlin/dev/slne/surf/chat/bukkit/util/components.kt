package dev.slne.surf.chat.bukkit.util

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.bukkit.hook.MiniPlaceholdersHook
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

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

        Bukkit.getServer().deleteMessage(signature)
        Bukkit.getOnlinePlayers()
            .filter { online -> online.hasPermission(PermissionRegistry.TEAM_NOTIFY_DELETION) }
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

fun SurfComponentBuilder.appendName(player: Player) = append {
    append(
        MiniPlaceholdersHook.parse(player, "<luckperms_prefix><player_name>")
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
    darkSpacer(">>")
    appendSpace()
    error("AUTOMOD", TextDecoration.BOLD)
    appendSpace()
    spacer("|")
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

fun SurfComponentBuilder.appendLinePrefix() = darkSpacer(">")

fun SurfComponentBuilder.appendMessageData(messageData: MessageData) = append(buildText {
    appendLinePrefix()
    appendSpace()
    spacer("von")
    appendSpace()
    variableValue(messageData.sender.name)

    appendNewline()
    appendLinePrefix()
    appendSpace()
    spacer("am")
    appendSpace()
    variableValue(messageData.sentAt.formatTime())

    appendNewline()
    appendLinePrefix()
    appendSpace()
    spacer("auf")
    appendSpace()
    variableValue(messageData.server)
})

private val zone = ZoneId.of("Europe/Berlin")
fun Long.formatTime(): String =
    ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), zone).format(timeFormatter)

fun Long.formatAgo(): String {
    val then = Instant.ofEpochMilli(this)
    val now = Instant.now()

    val totalSeconds = ChronoUnit.SECONDS.between(then, now)
    if (totalSeconds < 1) return "now"

    val seconds = totalSeconds % 60
    val totalMinutes = totalSeconds / 60
    val minutes = totalMinutes % 60
    val totalHours = totalMinutes / 60
    val hours = totalHours % 24
    val totalDays = totalHours / 24
    val days = totalDays % 30
    val totalMonths = totalDays / 30
    val months = totalMonths % 12
    val years = totalMonths / 12

    return when {
        years > 0 -> "%d.%02dy ago".format(years, months)
        totalMonths > 0 -> "%d.%02dmo ago".format(totalMonths, days)
        totalDays > 0 -> "%d.%02dd ago".format(totalDays, hours)
        totalHours > 0 -> "%d.%02dh ago".format(totalHours, minutes)
        totalMinutes > 0 -> "%d.%02dm ago".format(totalMinutes, seconds)
        else -> "%d.%02ds ago".format(seconds, 0)
    }
}