package dev.slne.surf.chat.velocity.command.denylist

import com.github.shynixn.mccoroutine.velocity.launch

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.PageableMessageBuilder
import dev.slne.surf.chat.velocity.util.formatTime
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickCopiesToClipboard
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

import net.kyori.adventure.text.Component

class DenyListListCommand(commandName: String) : CommandAPICommand(commandName) {

    init {
        withPermission(ChatPermissionRegistry.COMMAND_DENYLIST_LIST)
        integerArgument("page", 1, optional = true)

        playerExecutor { player, args ->
            container.launch {
                val result = databaseService.loadDenyList()
                val page = args.getOrDefaultUnchecked("page", 1)

                if (result.isEmpty()) {
                    player.sendText {
                        error("Es sind keine Wörter auf der Denylist.")
                    }
                    return@launch
                }

                PageableMessageBuilder {
                    title {
                        primary("Wörter auf der Denylist")
                        darkSpacer(" (${result.size} Einträge)")
                    }

                    result.forEach {
                        line {
                            darkSpacer(" - ")
                            append(Component.text(it.word, Colors.WHITE))
                            spacer(" (hinzugefügt von ${it.addedBy})")

                            hoverEvent(buildText {

                                primary("Eintrag: ")
                                info(it.word)
                                appendNewline()
                                primary("Hinzugefügt von: ")
                                info(it.addedBy)
                                appendNewline()
                                primary("Grund: ")
                                info(it.reason ?: "Kein Grund angegeben")
                                appendNewline()
                                primary("Datum: ")
                                info(it.addedAt.formatTime())
                                appendNewline()
                                darkSpacer("Klicke, um den Eintrag zu kopieren.")
                            })

                            clickCopiesToClipboard(it.word)
                        }
                    }

                    pageCommand = "/surfchat denylist list %page%"
                }.send(player, page)
            }
        }
    }
}