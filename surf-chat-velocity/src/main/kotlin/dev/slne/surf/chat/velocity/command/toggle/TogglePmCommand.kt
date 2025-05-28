package dev.slne.surf.chat.velocity.command.toggle

import com.github.shynixn.mccoroutine.velocity.launch

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.sendText
import net.kyori.adventure.text.format.TextDecoration

class TogglePmCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_TOGGLE_PM)
        playerExecutor { player, _ ->
            container.launch {
                val user = databaseService.getUser(player.uniqueId)
                val ignoring = user.togglePmsEnabled()

                if (ignoring) {
                    user.sendText {
                        success("Du ignorierst jetzt privaten Nachrichten. ")
                        append {
                            spacer("(Freunde können diese Sperre umgehen)").decorate(TextDecoration.ITALIC)
                        }
                    }
                } else {
                    user.sendText {
                        success("Du ignorierst keine privaten Nachrichten mehr.")
                    }
                }
            }
        }
    }
}