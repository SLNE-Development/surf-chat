package dev.slne.surf.chat.bukkit.util

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.Constants
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.text.BuildableComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentBuilder
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

private val zone = ZoneId.of("Europe/Berlin")
val timeFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("dd.MM.yyyy, HH:mm:ss", Locale.GERMANY)
    .withZone(zone)

fun Long.formatTime(): String =
    ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), zone).format(timeFormatter)

fun Long.formatAgo(): String {
    val diff = abs(System.currentTimeMillis() - this).milliseconds

    val minutes = diff.inWholeMinutes
    val hours = diff.inWholeHours
    val days = diff.inWholeDays
    val months = days / 30
    val years = days / 365

    val value: Double
    val unit: String

    when {
        years > 0 -> {
            value = diff.inWholeDays / 365.0; unit = "y"
        }

        months > 0 -> {
            value = diff.inWholeDays / 30.0; unit = "m"
        }

        days > 0 -> {
            value = diff.inWholeDays.toDouble(); unit = "d"
        }

        hours > 0 -> {
            value = diff.inWholeHours.toDouble(); unit = "h"
        }

        minutes > 0 -> {
            value = diff.inWholeMinutes.toDouble(); unit = "m"
        }

        else -> return "now"
    }

    return "%.2f%s ago".format(value, unit)
}

private val hexRegex = Regex("&#[A-Fa-f0-9]{6}")
fun convertLegacy(input: String) = hexRegex.replace(input) {
    "<#${it.value.removePrefix("&#")}>"
}

fun Cancellable.cancel() {
    isCancelled = true
}

fun sendTeamMessage(message: SurfComponentBuilder.() -> Unit) =
    Bukkit.getOnlinePlayers().filter { it.hasPermission(Constants.PERMISSION_TEAMCHAT) }
        .forEach { it.sendText(message) }

fun Component.plainText(): String = PlainTextComponentSerializer.plainText().serialize(this)
fun Channel.sendText(block: SurfComponentBuilder.() -> Unit) =
    members.forEach { it.sendText { block() } }

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

fun <C : BuildableComponent<C, B>, B : ComponentBuilder<C, B>> ComponentBuilder<C, B>.appendSpace(
    amount: Int,
) = repeat(amount) { appendSpace() }

fun PacketWrapper<*>.send(player: Player) =
    PacketEvents.getAPI().playerManager.sendPacket(player, this)

fun Component.remove(regex: Regex): Component {
    return this.replaceText { config ->
        config.match(regex.pattern).replacement("")
    }
}