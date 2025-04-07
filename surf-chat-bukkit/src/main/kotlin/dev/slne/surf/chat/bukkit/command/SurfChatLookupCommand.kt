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
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.OfflinePlayer
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class SurfChatLookupCommand(commandName: String): CommandAPICommand(commandName) {
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss dd.MM.yyyy")

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

                if(history.isEmpty()) {
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
                            variableValue(it.message)
                            hoverEvent(HoverEvent.showText(buildText {
                                primary("von: ")
                                info(target.name ?: target.uniqueId.toString())
                                appendNewline()
                                primary("Typ: ")
                                info(it.type)
                                appendNewline()
                                primary("Datum: ")
                                info(getString(it.timestamp))
                            }))
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

    private fun getString(unix: Long): String = timeFormatter.format(Instant.ofEpochSecond(unix).atZone(ZoneId.of("UTC+2")))
}