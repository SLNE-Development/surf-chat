package dev.slne.surf.chat.velocity.command.toggle

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.sendText

class ToggleSoundCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_TOGGLE_SOUND)
        playerExecutor { player, _ ->
            container.launch {
                val user = databaseService.getUser(player.uniqueId)
                val likesSound = user.togglePingsEnabled()

                if (likesSound) {
                    user.sendText {
                        success("Du hast Ping-Benachrichtigungen aktiviert.")
                    }
                } else {
                    user.sendText {
                        success("Du hast Ping-Benachrichtigungen deaktiviert.")
                    }
                }
            }
        }
    }
}