package dev.slne.surf.chat.bukkit.command.denylist

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.textArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendText
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class DenyListRemoveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_DENYLIST_REMOVE)
        textArgument("word")
        playerExecutor { player, args ->
            val word: String by args
            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val result = denylistService.removeFromDenylist(word)

                if (result) {
                    user.sendText(buildText {
                        success("Du hast das Wort ")
                        variableValue(word)
                        success(" von der Denylist entfernt.")
                    })
                } else {
                    user.sendText(buildText {
                        error("Das Wort ")
                        variableValue(word)
                        error(" befindet sich nicht auf der Denylist.")
                    })
                }
            }
        }
    }
}