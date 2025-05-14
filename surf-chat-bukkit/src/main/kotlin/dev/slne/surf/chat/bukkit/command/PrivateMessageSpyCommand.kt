package dev.slne.surf.chat.bukkit.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.command.argument.multiPlayerArgument
import dev.slne.surf.chat.core.service.spyService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.emptyObjectSet
import org.bukkit.entity.Player

class PrivateMessageSpyCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.pmspy")
        multiPlayerArgument("players", optional = true)

        playerExecutor { player, args ->
            val players = args.getOrDefaultUnchecked("players", emptyObjectSet<Player>())

            if (players.isEmpty()) {
                if (!spyService.isPrivateMessageSpying(player)) {
                    surfChatApi.sendText(player, buildText {
                        error("Du spionierst in keinen privaten Nachrichten.")
                    })
                    return@playerExecutor
                }

                spyService.clearPrivateMessageSpys(player)
                surfChatApi.sendText(player, buildText {
                    success("Du spionierst in keinen privaten Nachrichten mehr.")
                })
            } else {
                players.forEach {
                    spyService.addPrivateMessageSpy(player, it)
                }

                surfChatApi.sendText(player, buildText {
                    success("Du spionierst jetzt in den privaten Nachrichten von ")
                    variableValue(players.joinToString(", ") { it.name })
                    success(".")
                })
            }
        }
    }
}