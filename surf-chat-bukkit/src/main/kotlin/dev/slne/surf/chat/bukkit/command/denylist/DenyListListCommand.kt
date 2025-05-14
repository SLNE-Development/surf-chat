package dev.slne.surf.chat.bukkit.command.denylist

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.PageableMessageBuilder
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class DenyListListCommand(commandName: String) : CommandAPICommand(commandName) {
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter
        .ofPattern("dd.MM.yyyy, HH:mm:ss", Locale.GERMANY)
        .withZone(ZoneId.of("Europe/Berlin"))

    init {
        integerArgument("page", 1, optional = true)

        playerExecutor { player, args ->
            plugin.launch {
                val result = databaseService.loadDenyList()
                val page = args.getOrDefaultUnchecked("page", 1)

                if (result.isEmpty()) {
                    surfChatApi.sendText(player, buildText {
                        error("Es sind keine Wörter auf der Denylist.")
                    })
                    return@launch
                }

                PageableMessageBuilder {
                    title {
                        primary("Wörter auf der Denylist")
                        darkSpacer(" (${result.size} Einträge)")
                    }

                    result.forEach {
                        line {
                            darkSpacer(" - ")
                            append(Component.text(it.word, Colors.WHITE))
                            spacer(" (hinzugefügt von ${it.addedBy})")

                            hoverEvent(HoverEvent.showText(buildText {

                                primary("Eintrag: ")
                                info(it.word)
                                appendNewline()
                                primary("Hinzugefügt von: ")
                                info(it.addedBy)
                                appendNewline()
                                primary("Grund: ")
                                info(it.reason)
                                appendNewline()
                                primary("Datum: ")
                                info(getString(it.addedAt))
                                appendNewline()
                                darkSpacer("Klicke, um den Eintrag zu kopieren.")
                            }))

                            clickEvent(ClickEvent.copyToClipboard(it.word))
                        }
                    }

                    pageCommand = "/surfchat denylist list %page%"
                }.send(player, page)
            }
        }
    }

    private fun getString(unixMillis: Long): String {
        return timeFormatter.format(Instant.ofEpochMilli(unixMillis))
    }
}