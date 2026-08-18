package dev.slne.surf.chat.core.client.util

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private val zone = ZoneId.of("Europe/Berlin")
val timeFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("dd.MM.yyyy, HH:mm:ss", Locale.GERMANY)
    .withZone(zone)

fun OffsetDateTime.unixTime(): String = formatTime()

fun OffsetDateTime.formatTime(): String = atZoneSameInstant(zone).format(timeFormatter)

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
