package dev.slne.surf.chat.velocity.command.surfchat

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.sendText

class SurfChatChatClearCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_SURFCHAT_CLEAR)
        playerExecutor { player, _ ->
            historyService.clearChat()

            container.launch {
                val user = databaseService.getUser(player.uniqueId)

                user.sendText {
                    success("Du hast den Chat geleert.")
                }
            }
        }
    }
}
