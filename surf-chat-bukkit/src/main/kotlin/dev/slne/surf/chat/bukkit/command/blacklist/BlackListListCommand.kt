package dev.slne.surf.chat.bukkit.command.blacklist

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

class BlackListListCommand(commandName: String): CommandAPICommand(commandName) {
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter
        .ofPattern("HH:mm:ss, dd.MM.yyyy", Locale.GERMANY)
        .withZone(ZoneId.of("Europe/Berlin"))

    init {
        integerArgument("page", 1, optional = true)

        playerExecutor { player, args ->
            plugin.launch {
                val result = databaseService.loadBlacklist()
                val page = args.getOrDefaultUnchecked("page", 1)

                if(result.isEmpty()) {
                    surfChatApi.sendText(player, buildText {
                        error("Es sind keine Wörter auf der Blacklist.")
                    })
                    return@launch
                }

                PageableMessageBuilder {
                    title {
                        primary("Wörter auf der Blacklist")
                        darkSpacer(" (${result.size} Einträge)")
                    }

                    result.forEach {
                        line {
                            darkSpacer(" - ")
                            append(Component.text(it.word, Colors.WHITE))

                            hoverEvent(HoverEvent.showText(buildText {
                                primary("von: ")
                                info(it.addedBy)
                                appendNewline()
                                primary("Datum: ")
                                info(getString(it.addedAt))
                                appendNewline()
                                darkSpacer("Klicke, um die Nachricht zu kopieren.")
                            }))

                            clickEvent(ClickEvent.copyToClipboard(it.word))
                        }
                    }

                    pageCommand = "/surfchat blacklist list %page%"
                }.send(player, page)
            }
        }
    }

    private fun getString(unixMillis: Long): String {
        return timeFormatter.format(Instant.ofEpochMilli(unixMillis))
    }
}