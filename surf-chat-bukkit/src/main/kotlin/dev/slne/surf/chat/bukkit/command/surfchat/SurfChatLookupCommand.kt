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
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
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
                    "--deletedBy",
                    "--page",
                    "--server",
                    "--id"
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

                user.sendPrefixed {
                    info("Chat Verlauf wird geladen. Dies kann einige Sekunden dauern...")
                }

                val history = databaseService.loadHistory(
                    uuid = target.parse(),
                    type = parsed.type,
                    rangeMillis = parsed.range,
                    message = parsed.message,
                    deletedBy = parsed.deletedBy,
                    server = parsed.server,
                    id = parsed.id
                ).sortedByDescending { it.timestamp }

                if (history.isEmpty()) {
                    user.sendPrefixed {
                        error("Es wurden keine passenden Chat-Daten gefunden.")
                    }
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
                        primary("Chat-Daten".toSmallCaps())
                        target.player?.let {
                            primary(" von ".toSmallCaps())
                            variableValue(it.name?.toSmallCaps() ?: it.uniqueId.toString())
                        }
                        spacer(" (${history.size} Einträge)")
                    }

                    entriesWithNames.forEach { (entry, username) ->
                        line {
                            darkSpacer(" - ")
                            text(entry.message, Colors.WHITE)
                            spacer(" (${username})")

                            if (entry.deletedBy != null) {
                                appendNewline()
                                spacer("    (Gelöscht von ${entry.deletedBy})").decorate(
                                    TextDecoration.ITALIC
                                )
                            }

                            hoverEvent(buildText {
                                variableKey("von: ".toSmallCaps())
                                variableValue(username)
                                appendNewline()
                                variableKey("Typ: ".toSmallCaps())
                                variableValue(entry.type.name)
                                appendNewline()
                                variableKey("Datum: ".toSmallCaps())
                                variableValue(formatTime(entry.timestamp))
                                appendNewline()
                                variableKey("Server: ".toSmallCaps())
                                variableValue(entry.server)
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