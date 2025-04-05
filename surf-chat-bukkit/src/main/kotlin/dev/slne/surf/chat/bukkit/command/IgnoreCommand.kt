package dev.slne.surf.chat.bukkit.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.offlinePlayerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.OfflinePlayer

class IgnoreCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.ignore")
        offlinePlayerArgument("target")

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
                        primary("Du ignorierst jetzt ")
                        info(target.name ?: target.uniqueId.toString())
                        primary(".")
                    })
                } else {
                    user.sendText(buildText {
                        primary("Du ignorierst ")
                        info(target.name ?: target.uniqueId.toString())
                        primary(" nicht mehr.")
                    })
                }
            }
        }
    }
}
