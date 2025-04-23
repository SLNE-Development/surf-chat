package dev.slne.surf.chat.bukkit.command.surfchat

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.chat.bukkit.command.argument.multiOfflinePlayerArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.LookupFlags
import dev.slne.surf.chat.bukkit.util.MultiPlayerSelectorData
import dev.slne.surf.chat.bukkit.util.PageableMessageBuilder
import dev.slne.surf.chat.bukkit.util.formatTime
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.TextDecoration

class SurfChatLookupCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.lookup")

        multiOfflinePlayerArgument("target")
        argument(GreedyStringArgument("filters").replaceSuggestions (ArgumentSuggestions.strings("--type", "--range", "--message", "--deleted", "--deletedBy", "--page", "--server"))
            .setOptional(true)
        )


        playerExecutor { sender, args ->
            val flagString = args.getOrDefaultUnchecked("filters", "")
            val parsed = LookupFlags.parse(flagString)
            val page = parsed.page ?: 1

            val target = args.getUnchecked<MultiPlayerSelectorData>("target") ?: return@playerExecutor


            plugin.launch {
                val user = databaseService.getUser(sender.uniqueId)

                user.sendText(buildText {
                    primary("Chat-Daten werden geladen...")
                })

                val history = databaseService.loadHistory(
                    uuid = target.parse(),
                    type = parsed.type,
                    rangeMillis = parsed.range,
                    message = parsed.message,
                    deleted = parsed.deleted,
                    deletedBy = parsed.deletedBy,
                    server = parsed.server
                ).sortedByDescending { it.timestamp }

                if (history.isEmpty()) {
                    user.sendText(buildText {
                        error("Es wurden keine passenden Chat Daten gefunden. Bitte überprüfe deinen Filter (${parsed.toFlagString()})")
                    })
                    return@launch
                }

                PageableMessageBuilder {
                    pageCommand = "/surfchat lookup ${target.getString()} ${parsed.toFlagString()} --page %page%"

                    title {
                        primary("Chat-Daten")
                        target.player?.let {
                            primary(" von ")
                            info(it.name ?: it.uniqueId.toString())
                        }
                        spacer(" (${history.size} Einträge)")
                    }

                    history.forEach {
                        line {
                            darkSpacer(" - ")
                            append(Component.text(it.message, Colors.WHITE))
                            spacer(" (${it.type})")

                            if (it.deleted) {
                                appendNewline()
                                spacer("    (Gelöscht von ${it.deletedBy})").decorate(TextDecoration.ITALIC)
                            }

                            hoverEvent(HoverEvent.showText(buildText {
                                primary("von: ")
                                info(it.uuid.toString())
                                appendNewline()
                                primary("Typ: ")
                                info(it.type)
                                appendNewline()
                                primary("Datum: ")
                                info(formatTime(it.timestamp))
                                appendNewline()
                                primary("Server: ")
                                info(it.server)
                                appendNewline()
                                darkSpacer("Klicke, um die Nachricht zu kopieren.")
                            }))

                            clickEvent(ClickEvent.copyToClipboard(it.message))
                        }
                    }
                }.send(sender, page)
            }
        }
    }
}