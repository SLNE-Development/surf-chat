package dev.slne.surf.chat.velocity.command.denylist

import com.github.shynixn.mccoroutine.velocity.launch

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.textArgument

import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.sendText

class DenyListRemoveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_DENYLIST_REMOVE)
        textArgument("word")
        playerExecutor { player, args ->
            val word: String by args
            container.launch {
                val user = databaseService.getUser(player.uniqueId)
                val result = denylistService.removeFromDenylist(word)

                if (result) {
                    user.sendText {
                        success("Du hast das Wort ")
                        variableValue(word)
                        success(" von der Denylist entfernt.")
                    }
                } else {
                    user.sendText {
                        error("Das Wort ")
                        variableValue(word)
                        error(" befindet sich nicht auf der Denylist.")
                    }
                }
            }
        }
    }
}