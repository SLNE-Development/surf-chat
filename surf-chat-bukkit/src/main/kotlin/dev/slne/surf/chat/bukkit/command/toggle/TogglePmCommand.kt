package dev.slne.surf.chat.bukkit.command.toggle

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendText
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class TogglePmCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_TOGGLE_PM)
        playerExecutor { player, _ ->
            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val ignoring = user.togglePm()

                if (ignoring) {
                    user.sendText(buildText {
                        success("Du ignorierst jetzt privaten Nachrichten. ")
//                        append {
//                            spacer("(Freunde können diese Sperre umgehen)").decorate(TextDecoration.ITALIC)
//                        }
                    })
                } else {
                    user.sendText(buildText {
                        success("Du ignorierst keine privaten Nachrichten mehr.")
                    })
                }
            }
        }
    }
}