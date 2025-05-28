package dev.slne.surf.chat.velocity.command.surfchat

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.kotlindsl.argument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument

import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.plugin
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.LookupFlags
import dev.slne.surf.chat.velocity.util.PageableMessageBuilder
import dev.slne.surf.chat.velocity.util.formatTime
import dev.slne.surf.chat.velocity.util.getUsername
import dev.slne.surf.chat.velocity.util.sendText
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickCopiesToClipboard
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import net.kyori.adventure.text.format.TextDecoration
import java.util.UUID

class SurfChatLookupCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_SURFCHAT_LOOKUP)

        withArguments(StringArgument("target").replaceSuggestions(ArgumentSuggestions.stringCollection {
            plugin.proxy.allPlayers.map { it.username }.also { "--all" }
        }))
        argument(
            GreedyStringArgument("filters").replaceSuggestions(
                ArgumentSuggestions.strings(
                    "--type",
                    "--range",
                    "--message",
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

            val target = args.getUnchecked<String>("target") ?: return@playerExecutor


            container.launch {
                val user = databaseService.getUser(sender.uniqueId)
                val lookupTarget = LookupTarget(target).parse()

                user.sendText {
                    info("Chat-Daten werden geladen...")
                }

                val history = databaseService.loadHistory (
                    uuid = lookupTarget,
                    type = parsed.type,
                    rangeMillis = parsed.range,
                    message = parsed.message,
                    deletedBy = parsed.deletedBy,
                    server = parsed.server
                ).sortedByDescending { it.timestamp }

                if (history.isEmpty()) {
                    user.sendText {
                        error("Es wurden keine passenden Chat-Daten gefunden. Bitte überprüfe deinen Filter (${parsed.toFlagString()})")
                    }
                    return@launch
                }

                val entriesWithNames = history.map { entry ->
                    val username = entry.userUuid.getUsername()
                    entry to username
                }

                PageableMessageBuilder {
                    pageCommand =
                        "/surfchat lookup $target ${parsed.toFlagString()} --page %page%"

                    title {
                        info("Chat-Daten".toSmallCaps())
                        lookupTarget?.let {
                            info(" von ".toSmallCaps())
                            variableValue(target.toSmallCaps())
                        }
                        spacer(" (${history.size} Einträge)")
                    }

                    entriesWithNames.forEach { (entry, username) ->
                        line {
                            darkSpacer(" - ")
                            text(entry.message, Colors.WHITE)
                            spacer(" (${entry.type})")

                            if (entry.deletedBy != null) {
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
                                info(entry.timestamp.formatTime())
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

    data class LookupTarget (
        val input: String
    ) {
        suspend fun parse(): UUID? {
            if(input == "--all") {
                return null
            }
            return PlayerLookupService.getUuid(input)
        }
    }
}