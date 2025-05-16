package dev.slne.surf.chat.bukkit.command.toggle

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.databaseService

class ToggleSoundCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_TOGGLE_SOUND)
        playerExecutor { player, _ ->
            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val likesSound = user.toggleSound()

                if (likesSound) {
                    user.sendPrefixed {
                        success("Du hast Benachrichtigungen aktiviert.")
                    }
                } else {
                    user.sendPrefixed {
                        success("Du hast Benachrichtigungen deaktiviert.")
                    }
                }
            }
        }
    }
}