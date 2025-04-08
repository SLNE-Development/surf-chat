package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.offlinePlayerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.PageableMessageBuilder
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.OfflinePlayer
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class SurfChatLookupCommand(commandName: String): CommandAPICommand(commandName) {
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter
        .ofPattern("HH:mm:ss, dd.MM.yyyy", Locale.GERMANY)
        .withZone(ZoneId.of("Europe/Berlin"))

    init {
        withPermission("surf.chat.command.lookup")
        offlinePlayerArgument("target")
        integerArgument("page", min = 1, optional = true)

        playerExecutor { player, args ->
            val target: OfflinePlayer by args
            val page = args.getOrDefaultUnchecked("page", 1)

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)

                user.sendText(buildText {
                    primary("Die Daten von ")
                    info(target.name ?: target.uniqueId.toString())
                    primary(" werden geladen...")
                })

                val history = databaseService.loadHistory(target.uniqueId).sortedBy { it.timestamp }

                if (history.isEmpty()) {
                    user.sendText(buildText {
                        error("Es sind keine Chat-Daten für diesen Spieler vorhanden.")
                    })
                    return@launch
                }

                val builder = PageableMessageBuilder {
                    pageCommand = "/surfchat lookup ${target.name ?: target.uniqueId} %page%"

                    history.forEach {
                        line {
                            darkSpacer(" - ")
                            append(Component.text(it.message, Colors.WHITE))
                            spacer(" (${it.type})")

                            if(it.deleted) {
                                appendNewline()
                                spacer("     (Gelöscht von ${it.deletedBy})").decorate(TextDecoration.ITALIC)
                            }

                            hoverEvent(HoverEvent.showText(buildText {
                                primary("von: ")
                                info(target.name ?: target.uniqueId.toString())
                                appendNewline()
                                primary("Typ: ")
                                info(it.type)
                                appendNewline()
                                primary("Datum: ")
                                info(getString(it.timestamp))
                                appendNewline()
                                darkSpacer("Klicke, um die Nachricht zu kopieren.")
                            }))
                            clickEvent(ClickEvent.copyToClipboard(it.message))
                        }
                    }
                }

                builder.title {
                    primary("Chat Daten von ")
                    info(target.name ?: target.uniqueId.toString())
                }

                builder.send(player, page)
            }
        }
    }

    private fun getString(unixMillis: Long): String {
        return timeFormatter.format(Instant.ofEpochMilli(unixMillis))
    }
}