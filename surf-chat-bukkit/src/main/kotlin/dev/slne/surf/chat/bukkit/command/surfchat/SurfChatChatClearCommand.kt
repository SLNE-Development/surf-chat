package dev.slne.surf.chat.bukkit.command.surfchat

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendText
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.core.service.historyService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class SurfChatChatClearCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_SURFCHAT_CLEAR)
        playerExecutor { player, _ ->
            historyService.clearChat()

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)

                user.sendText(buildText {
                    success("Du hast den Chat geleert.")
                })
            }
        }
    }
}
