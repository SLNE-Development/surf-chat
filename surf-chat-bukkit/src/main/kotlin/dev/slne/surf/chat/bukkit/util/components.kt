package dev.slne.surf.chat.bukkit.util

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.chat.api.message.MessageData
import dev.slne.surf.chat.bukkit.hook.LuckPermsHook
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import kotlinx.coroutines.launch
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

fun SurfComponentBuilder.appendDelete(messageData: MessageData) = append(buildText {
    darkSpacer("[")
    error("X")
    darkSpacer("]")
    darkSpacer(" ")
    clickEvent(ClickEvent.callback { clicked ->
        val signature = messageData.signature ?: run {
            clicked.sendText {
                appendErrorPrefix()
                error("Die Nachricht besitzt eine ungültige Signatur und konnte nicht gelöscht werden.")
            }
            return@callback
        }


        Bukkit.getServer().deleteMessage(signature)

        plugin.launch {
            launch {
                historyService.markDeleted(messageData.messageUuid, clicked.uuid())
            }

            val message = buildText {
                appendInfoPrefix()
                variableValue(clicked.name())
                info(" hat eine Nachricht von ")
                variableValue(PlayerLookupService.getUsername(messageData.sender) ?: messageData.sender.toString())
                info(" gelöscht: ")
                append(messageData.message)
            }

            Bukkit.broadcast(message, PermissionRegistry.TEAM_NOTIFY_DELETION)
        }
    })
    hoverEvent(buildText {
        warning("Klicke, um die Nachricht zu löschen")
    })
})

fun SurfComponentBuilder.appendName(player: Player) = append {
    append(MiniMessage.miniMessage().deserialize(LuckPermsHook.getPrefix(player) + player.name))
}

fun SurfComponentBuilder.appendTeleport(name: String, uuid: UUID) = append {
    darkSpacer("[")
    info("TP")
    darkSpacer("]")
    darkSpacer(" ")
    clickEvent(ClickEvent.callback(ClickCallback.widen({ player ->
        val target = Bukkit.getPlayer(uuid) ?: return@widen
        player.teleportAsync(target.location)
    }, Player::class.java)))
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

fun SurfComponentBuilder.appendMessageData(senderName: String, messageData: MessageData) = append {
    appendLinePrefix()
    appendSpace()
    spacer("von")
    appendSpace()
    variableValue(senderName)

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
}

private val zone = ZoneId.of("Europe/Berlin")
fun OffsetDateTime.formatTime(): String = this.atZoneSameInstant(zone).format(timeFormatter)

fun OffsetDateTime.formatAgo(): String {
    val then = toInstant()
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