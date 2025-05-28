package dev.slne.surf.chat.velocity.command.ignore

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand

import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.plugin
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService

class IgnoreCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_IGNORE)
        withArguments(StringArgument("target").replaceSuggestions(ArgumentSuggestions.stringCollection {
            plugin.proxy.allPlayers.map { it.username }
        }))
        subcommand(IgnoreListCommand("#list"))

        playerExecutor { player, args ->
            val target: String by args

            container.launch {
                val targetUuid = PlayerLookupService.getUuid(target) ?: run {
                    player.sendText {
                        error("Der Spieler '$target' konnte nicht gefunden werden.")
                    }
                    return@launch
                }

                if(targetUuid == player.uniqueId) {
                    player.sendText {
                        error("Du kannst dich nicht selbst ignorieren.")
                    }
                    return@launch
                }

                val user = databaseService.getUser(player.uniqueId)

                if (user.toggleIgnore(targetUuid)) {
                    user.sendText {
                        success("Du ignorierst jetzt ")
                        variableValue(target)
                        success(".")
                    }
                } else {
                    user.sendText {
                        success("Du ignorierst ")
                        variableValue(target)
                        success(" nicht mehr.")
                    }
                }
            }
        }
    }
}