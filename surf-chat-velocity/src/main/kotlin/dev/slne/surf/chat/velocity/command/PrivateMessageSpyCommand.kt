package dev.slne.surf.chat.velocity.command

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.proxy.Player
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.core.service.databaseService

import dev.slne.surf.chat.core.service.spyService
import dev.slne.surf.chat.velocity.command.argument.multiPlayerArgument
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.toChatUser
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.emptyObjectSet

class PrivateMessageSpyCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_MSGSPY)
        multiPlayerArgument("players", optional = true)

        playerExecutor { player, args ->
            val players = args.getOrDefaultUnchecked("players", emptyObjectSet<Player>())

            container.launch {
                val user = databaseService.getUser(player.uniqueId)

                if (players.isEmpty()) {
                    if (!spyService.isPrivateMessageSpying(user)) {
                        player.sendText {
                            error("Du spionierst in keinen privaten Nachrichten.")
                        }
                        return@launch
                    }

                    spyService.clearPrivateMessageSpys(user)
                    player.sendText {
                        success("Du spionierst in keinen privaten Nachrichten mehr.")
                    }
                } else {
                    players.forEach {
                        spyService.addPrivateMessageSpy(user, it.toChatUser())
                    }

                    player.sendText {
                        success("Du spionierst jetzt in den privaten Nachrichten von ")
                        variableValue(players.joinToString(", ") { it.username })
                        success(".")
                    }
                }
            }
        }
    }
}