package dev.slne.surf.chat.server.util

import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

private val zone = ZoneId.of("Europe/Berlin")
val timeFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("dd.MM.yyyy, HH:mm:ss", Locale.GERMANY)
    .withZone(zone)

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
        years > 0 -> "%d.%02d y ago".format(years, months)
        totalMonths > 0 -> "%d.%02d mo ago".format(totalMonths, days)
        totalDays > 0 -> "%d.%02d d ago".format(totalDays, hours)
        totalHours > 0 -> "%d.%02d h ago".format(totalHours, minutes)
        totalMinutes > 0 -> "%d.%02d m ago".format(totalMinutes, seconds)
        else -> "%d.%02d s ago".format(seconds, 0)
    }
}

private val hexRegex = Regex("&#[A-Fa-f0-9]{6}")
fun convertLegacy(input: String) = hexRegex.replace(input) {
    "<#${it.value.removePrefix("&#")}>"
}

fun Long.coloredComponent(good: Long = 200L, okay: Long = 1000L) =
    buildText {
        when {
            this@coloredComponent < good -> append(
                Component.text(
                    this@coloredComponent.toString() + "ms",
                    Colors.GREEN
                )
            )

            this@coloredComponent < okay -> append(
                Component.text(
                    this@coloredComponent.toString() + "ms",
                    Colors.YELLOW
                )
            )

            else -> append(Component.text(this@coloredComponent.toString() + "ms", Colors.RED))
        }
    }

fun TextColor.miniMessage() =
    "<${this.asHexString()}>"
