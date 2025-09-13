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