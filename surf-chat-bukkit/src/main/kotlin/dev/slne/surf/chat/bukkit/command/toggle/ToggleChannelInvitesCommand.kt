package dev.slne.surf.chat.bukkit.command.toggle

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendText
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ToggleChannelInvitesCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_TOGGLE_INVITES)
        playerExecutor { player, _ ->
            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val receiving = user.toggleChannelInvites()

                if (receiving) {
                    user.sendText(buildText {
                        success("Du ignorierst keine Nachrichtenkanal-Einladungen mehr.")
                    })
                } else {
                    user.sendText(buildText {
                        success("Du ignorierst jetzt Nachrichtenkanal-Einladungen.")
                    })
                }
            }
        }
    }
}