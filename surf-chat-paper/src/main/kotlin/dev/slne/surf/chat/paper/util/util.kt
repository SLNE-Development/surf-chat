package dev.slne.surf.chat.paper.util

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

private val zone = ZoneId.of("Europe/Berlin")
val timeFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("dd.MM.yyyy, HH:mm:ss", Locale.GERMANY)
    .withZone(zone)

fun OffsetDateTime.unixTime(): String = atZoneSameInstant(zone).format(timeFormatter)

private val hexRegex = Regex("&#[A-Fa-f0-9]{6}")
fun convertLegacy(input: String) = hexRegex.replace(input) {
    "<#${it.value.removePrefix("&#")}>"
}

fun Cancellable.cancel() {
    isCancelled = true
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

fun PacketWrapper<*>.send(player: Player) =
    PacketEvents.getAPI().playerManager.sendPacket(player, this)

fun Component.remove(regex: Regex): Component {
    return this.replaceText { config ->
        config.match(regex.pattern).replacement("")
    }
}