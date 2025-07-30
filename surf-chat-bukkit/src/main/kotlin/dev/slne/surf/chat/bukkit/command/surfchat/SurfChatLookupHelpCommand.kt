package dev.slne.surf.chat.bukkit.command.surfchat

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.MessageType
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.appendSpace
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.surfChatLookupHelpCommand() = subcommand("lookuphelp") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SURFCHAT_LOOKUP_HELP)
    playerExecutor { player, _ ->
        val pagination = Pagination<HelpEntry> {
            title {
                primary("Lookup Help".toSmallCaps(), TextDecoration.BOLD)
            }

            rowRenderer { help, _ ->
                listOf(
                    buildText {
                        append(CommonComponents.EM_DASH)
                        appendSpace()
                        variableKey(help.prefix)
                        spacer(":")
                        appendSpace()
                        variableValue(help.content)
                        appendNewline()
                        appendSpace(5)
                        spacer("z.B.")
                        appendSpace()
                        spacer(help.example)
                    }
                )
            }
        }

        player.sendText {
            append(
                pagination.renderComponent(
                    listOf(
                        HelpEntry(
                            "type",
                            "Nach Nachrichtentyp filtern. Mögliche Werte: ${
                                MessageType.entries.joinToString(
                                    ", "
                                ) { it.name.lowercase() }
                            }", "--type team"
                        ),
                        HelpEntry(
                            "range",
                            "Nach Zeitbereich filtern. Zeitangabe z.B. 1d, 10m, 5h, 10s, 7w.",
                            "--range 1d"
                        ),
                        HelpEntry(
                            "message",
                            "Nach grobem Nachrichteninhalt filtern.",
                            "--message \"Hallo Welt\""
                        ),
                        HelpEntry(
                            "deletedBy",
                            "Nach dem Spieler filtern, der die Nachricht gelöscht hat.",
                            "--deletedBy Jo_field"
                        ),
                        HelpEntry(
                            "limit",
                            "Anzahl der Ergebnisse. Die Ergebnisse sind nach Zeit sortiert.",
                            "--limit 10"
                        ),
                        HelpEntry(
                            "page",
                            "Seite der Ergebnisse.",
                            "--page 2"
                        ),
                        HelpEntry(
                            "server",
                            "Nach Server filtern.",
                            "--server survival01"
                        ),
                        HelpEntry(
                            "channel",
                            "Nach Kanal filtern.",
                            "--channel testkanal"
                        ),
                        HelpEntry(
                            "sender",
                            "Nach dem Sender der Nachricht filtern.",
                            "--sender TheBjoRedCraft"
                        ),
                        HelpEntry(
                            "receiver",
                            "Nach dem Empfänger der Nachricht filtern.",
                            "--receiver Keviro"
                        ),
                        HelpEntry(
                            "messageUuid",
                            "Nach der Uuid der Nachricht filtern.",
                            "--messageUuid 1c779cb1-3860-4e23-9cac-7f160b2acc61"
                        )
                    )
                )
            )
        }
    }
}

private data class HelpEntry(
    val prefix: String,
    val content: String,
    val example: String
)