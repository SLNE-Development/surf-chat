package dev.slne.surf.chat.fallback.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

val timeFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("dd.MM.yyyy, HH:mm:ss", Locale.GERMANY)
    .withZone(ZoneId.of("Europe/Berlin"))

fun Long.formatTime(): String {
    return timeFormatter.format(Instant.ofEpochMilli(this))
}

fun debug(message: Any) {
    println("[SurfChat-Debug] $message")
}

fun Component.toPlainText(): String {
    return PlainTextComponentSerializer.plainText().serialize(this)
}