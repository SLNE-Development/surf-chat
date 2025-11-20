package dev.slne.surf.chat.paper.util

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