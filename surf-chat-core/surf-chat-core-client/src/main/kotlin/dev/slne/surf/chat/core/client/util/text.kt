package dev.slne.surf.chat.core.client.util

import dev.slne.surf.api.core.messages.Colors
import dev.slne.surf.api.core.messages.adventure.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.format.TextColor
import java.net.URI

private val hexRegex = Regex("&#[A-Fa-f0-9]{6}")
fun convertLegacy(input: String) = hexRegex.replace(input) {
    "<#${it.value.removePrefix("&#")}>"
}

fun Component.remove(regex: Regex): Component {
    return this.replaceText { config ->
        config.match(regex.pattern).replacement("")
    }
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

private val linkRegex = Regex("(?i)\\b((https?://)?[\\w-]+(\\.[\\w-]+)+(/\\S*)?)\\b")

fun updateLinks(rawMessage: Component): Component {
    var message = rawMessage
    val text = rawMessage.plain()

    linkRegex.findAll(text).forEach { match ->
        runCatching {
            val url = if (match.value.startsWith("http://") || match.value.startsWith("https://")) {
                match.value
            } else {
                "https://${match.value}"
            }

            val uri = URI(url)
            uri.toURL()

            message = message.replaceText(
                TextReplacementConfig.builder()
                    .match(Regex.escape(match.value))
                    .replacement(
                        buildText {
                            text(match.value)
                            hoverEvent(buildText {
                                info("Klicke hier, um den Link zu öffnen.")
                            })
                            clickOpensUrl(url)
                        }
                    )
                    .build()
            )
        }
    }

    return message
}
