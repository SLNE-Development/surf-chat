package dev.slne.surf.chat.core.common.util

import dev.slne.surf.chat.core.common.message.MessageData
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.text.format.TextColor
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun SurfComponentBuilder.appendLinePrefix() = darkSpacer(">")

fun SurfComponentBuilder.appendMessageData(messageData: MessageData) = append(buildText {
    appendLinePrefix()
    info("von")
    appendSpace()
    variableValue(messageData.sender.name)

    appendNewline()
    appendLinePrefix()
    info("am")
    appendSpace()
    variableValue(messageData.sentAt.formatTime())

    appendNewline()
    appendLinePrefix()
    info("auf")
    appendSpace()
    variableValue(messageData.server)
})

private val zone = ZoneId.of("Europe/Berlin")
val timeFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("dd.MM.yyyy, HH:mm:ss", Locale.GERMANY)
    .withZone(zone)

fun Long.formatTime(): String =
    ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), zone).format(timeFormatter)

fun TextColor.miniMessage() =
    "<${this.asHexString()}>"