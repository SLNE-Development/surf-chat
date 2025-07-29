package dev.slne.surf.chat.bukkit.command.surfchat

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.MessageType
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.surfChatLookupHelpCommand() = subcommand("help") {
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
                    }
                )
            }
        }

        player.sendText {
            append(
                pagination.renderComponent(
                    listOf(
                        HelpEntry(
                            "--type",
                            "Nach Nachrichtentyp filtern. Mögliche Werte: ${
                                MessageType.entries.joinToString(
                                    ", "
                                ) { it.name.lowercase() }
                            }"
                        ),
                        HelpEntry(
                            "--range",
                            "Nach Zeitbereich filtern. Zeitangabe z.B. 1d, 10m, 5h, 10s, 7w. Beispiel: --range:1d"
                        ),
                        HelpEntry(
                            "--message",
                            "Nach grobem Nachrichteninhalt filtern. Beispiel: --message:\"Hallo Welt\""
                        ),
                        HelpEntry(
                            "--deletedBy",
                            "Nach dem Spieler filtern, der die Nachricht gelöscht hat. Beispiel: --deletedBy:Jo_field"
                        ),
                        HelpEntry(
                            "--limit",
                            "Anzahl der Ergebnisse. Die Ergebnisse sind nach Zeit sortiert. Beispiel: --limit:10"
                        ),
                        HelpEntry(
                            "--page",
                            "Seite der Ergebnisse. Die Seite kann über die Pfeile gewechselt werden. Manuelles Beispiel: --page:2"
                        ),
                        HelpEntry(
                            "--server",
                            "Nach Server filtern. Beispiel: --server:survival01"
                        ),
                        HelpEntry(
                            "--channel",
                            "Nach Kanal filtern. Beispiel: --channel:testkanal"
                        ),
                        HelpEntry(
                            "--sender",
                            "Nach dem Sender der Nachricht filtern. Beispiel: --sender:TheBjoRedCraft"
                        ),
                        HelpEntry(
                            "--receiver",
                            "Nach dem Empfänger der Nachricht filtern. Beispiel: --receiver:Keviro"
                        ),
                        HelpEntry(
                            "--messageUuid",
                            "Nach der Uuid der Nachricht filtern. Beispiel: --messageUuid:1c779cb1-3860-4e23-9cac-7f160b2acc61"
                        )
                    )
                )
            )
        }
    }
}

private data class HelpEntry(
    val prefix: String,
    val content: String
)