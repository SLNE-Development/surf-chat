package dev.slne.surf.chat.velocity.command.surfchat

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.formatTime
import dev.slne.surf.chat.velocity.util.getUsername
import dev.slne.surf.chat.velocity.util.isUuid
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.clickCopiesToClipboard
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.format.TextDecoration
import java.util.UUID

class SurfChatInfoCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        stringArgument("messageId")
        playerExecutor { player, args ->
            val messageId = args.getUnchecked<String>("messageId") ?: return@playerExecutor

            if(!messageId.isUuid()) {
                player.sendText {
                    appendPrefix()
                    error("Diese Nachrichten-ID ist ungültig.")
                }
                return@playerExecutor
            }

            container.launch {
                val historyEntry = databaseService.loadHistory(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    UUID.fromString(messageId)
                ).firstOrNull() ?: return@launch run {
                    player.sendText {
                        appendPrefix()
                        error("Es konnte keine Nachricht mit dieser ID gefunden werden.")
                    }
                }

                val userName = historyEntry.userUuid.getUsername()

                player.sendText {
                    info("Nachrichten-Info".toSmallCaps())
                    appendNewline()
                    append {
                        info("| ")
                        decorate(TextDecoration.BOLD)
                    }
                    variableKey("Inhalt: ".toSmallCaps())
                    variableValue(historyEntry.message)
                    appendNewline()
                    append {
                        info("| ")
                        decorate(TextDecoration.BOLD)
                    }
                    variableKey("Author: ".toSmallCaps())
                    variableValue(userName)
                    appendNewline()
                    append {
                        info("| ")
                        decorate(TextDecoration.BOLD)
                    }
                    variableKey("Typ: ".toSmallCaps())
                    variableValue(historyEntry.type.name)
                    appendNewline()
                    append {
                        info("| ")
                        decorate(TextDecoration.BOLD)
                    }
                    variableKey("Datum: ".toSmallCaps())
                    variableValue(historyEntry.timestamp.formatTime())
                    appendNewline()
                    append {
                        info("| ")
                        decorate(TextDecoration.BOLD)
                    }
                    variableKey("Server: ".toSmallCaps())
                    variableValue(historyEntry.server)
                    appendNewline()
                    append {
                        info("| ")
                        decorate(TextDecoration.BOLD)
                    }
                    variableKey("Id: ".toSmallCaps())
                    variableValue(historyEntry.entryUuid.toString())
                    appendNewline()
                    spacer("Klicke, um die Nachricht-ID zu kopieren.")
                    clickCopiesToClipboard(historyEntry.entryUuid.toString())
                }
            }
        }
    }
}