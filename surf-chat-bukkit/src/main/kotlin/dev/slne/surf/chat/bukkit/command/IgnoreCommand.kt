package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.offlinePlayerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import org.bukkit.OfflinePlayer

class IgnoreCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.ignore")
        withArguments(EntitySelectorArgument.OneEntity("target"))

        playerExecutor { player, args ->
            val target: OfflinePlayer by args

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)

                if(target.uniqueId == user.uuid) {
                    user.sendText(buildText {
                        error("Du kannst dich nicht selbst ignorieren.")
                    })
                    return@launch
                }

                if(user.toggleIgnore(target.uniqueId)) {
                    user.sendText(buildText {
                        success("Du ignorierst jetzt ")
                        variableValue(target.requestName() ?: target.uniqueId.toString())
                        primary(".")
                    })
                } else {
                    user.sendText(buildText {
                        success("Du ignorierst ")
                        variableValue(target.requestName() ?: target.uniqueId.toString())
                        success(" nicht mehr.")
                    })
                }
            }
        }
    }

    suspend fun OfflinePlayer.requestName(): String? {
        return PlayerLookupService.getUsername(this.uniqueId)
    }
}
