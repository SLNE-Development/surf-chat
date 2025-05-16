package dev.slne.surf.chat.bukkit.command.ignore

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand

import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

class IgnoreCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_IGNORE)
        withArguments(EntitySelectorArgument.OnePlayer("target").replaceSuggestions(
            ArgumentSuggestions.stringCollection {
                val players = Bukkit.getOnlinePlayers().map { it.name }
                players.toSet()
            }))
        subcommand(IgnoreListCommand("#list"))

        playerExecutor { player, args ->
            val target: OfflinePlayer by args

            if(target.uniqueId == player.uniqueId) {
                player.sendPrefixed {
                    error("Du kannst dich nicht selbst ignorieren.")
                }
                return@playerExecutor
            }

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)

                if (user.toggleIgnore(target.uniqueId)) {
                    user.sendPrefixed {
                        success("Du ignorierst jetzt ")
                        variableValue(target.requestName() ?: target.uniqueId.toString())
                        success(".")
                    }
                } else {
                    user.sendPrefixed {
                        success("Du ignorierst ")
                        variableValue(target.requestName() ?: target.uniqueId.toString())
                        success(" nicht mehr.")
                    }
                }
            }
        }
    }

    suspend fun OfflinePlayer.requestName(): String? {
        return PlayerLookupService.getUsername(this.uniqueId)
    }
}