package dev.slne.surf.chat.bukkit.command.surfchat

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.command.argument.multiOfflinePlayerArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.*
import dev.slne.surf.chat.bukkit.util.utils.formatTime
import dev.slne.surf.chat.bukkit.util.utils.getUsername
import dev.slne.surf.chat.bukkit.util.utils.sendText
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickCopiesToClipboard
import net.kyori.adventure.text.format.TextDecoration

class SurfChatLookupCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_SURFCHAT_LOOKUP)

        multiOfflinePlayerArgument("target")
        argument(
            GreedyStringArgument("filters").replaceSuggestions(
                ArgumentSuggestions.strings(
                    "--type",
                    "--range",
                    "--message",
                    "--deleted",
                    "--deletedBy",
                    "--page",
                    "--server"
                )
            )
                .setOptional(true)
        )


        playerExecutor { sender, args ->
            val flagString = args.getOrDefaultUnchecked("filters", "")
            val parsed = LookupFlags.parse(flagString)
            val page = parsed.page ?: 1

            val target =
                args.getUnchecked<MultiPlayerSelectorData>("target") ?: return@playerExecutor


            plugin.launch {
                val user = databaseService.getUser(sender.uniqueId)

                user.sendText(buildText {
                    info("Chat-Daten werden geladen...")
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
                        error("Es wurden keine passenden Chat-Daten gefunden. Bitte überprüfe deinen Filter (${parsed.toFlagString()})")
                    })
                    return@launch
                }

                val entriesWithNames = history.map { entry ->
                    val username = entry.userUuid.getUsername()
                    entry to username
                }

                PageableMessageBuilder {
                    pageCommand =
                        "/surfchat lookup ${target.getString()} ${parsed.toFlagString()} --page %page%"

                    title {
                        primary("Chat-Daten")
                        target.player?.let {
                            primary(" von ")
                            info(it.name ?: it.uniqueId.toString())
                        }
                        spacer(" (${history.size} Einträge)")
                    }

                    entriesWithNames.forEach { (entry, username) ->
                        line {
                            darkSpacer(" - ")
                            text(entry.message, Colors.WHITE)
                            spacer(" (${entry.type})")

                            if (entry.deleted) {
                                appendNewline()
                                spacer("    (Gelöscht von ${entry.deletedBy})").decorate(
                                    TextDecoration.ITALIC
                                )
                            }

                            hoverEvent(buildText {
                                primary("von: ")
                                info(username)
                                appendNewline()
                                primary("Typ: ")
                                info(entry.type.name)
                                appendNewline()
                                primary("Datum: ")
                                info(formatTime(entry.timestamp))
                                appendNewline()
                                primary("Server: ")
                                info(entry.server)
                                appendNewline()
                                darkSpacer("Klicke, um die Nachricht zu kopieren.")
                            })

                            clickCopiesToClipboard(entry.message)
                        }
                    }
                }.send(sender, page)
            }
        }
    }
}