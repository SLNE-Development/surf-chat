package dev.slne.surf.chat.bukkit.util

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.Cancellable
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private val zone = ZoneId.of("Europe/Berlin")
val timeFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("dd.MM.yyyy, HH:mm:ss", Locale.GERMANY)
    .withZone(zone)

fun Long.unixTime(): String =
    ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), zone).format(timeFormatter)

private val hexRegex = Regex("&#[A-Fa-f0-9]{6}")
fun convertLegacy(input: String) = hexRegex.replace(input) {
    "<#${it.value.removePrefix("&#")}>"
}

fun Cancellable.cancel() {
    isCancelled = true
}

fun User.sendText(block: SurfComponentBuilder.() -> Unit) = player()?.sendText { block() }
fun Component.plainText(): String = PlainTextComponentSerializer.plainText().serialize(this)