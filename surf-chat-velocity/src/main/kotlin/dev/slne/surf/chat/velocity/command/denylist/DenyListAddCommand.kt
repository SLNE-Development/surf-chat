package dev.slne.surf.chat.velocity.command.denylist

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.textArgument
import dev.slne.surf.chat.api.model.DenyListEntry
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.sendText

class DenyListAddCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_DENYLIST_ADD)
        textArgument("word")
        greedyStringArgument("reason", optional = true)
        playerExecutor { player, args ->
            val word: String by args
            val reason: String? by args

            container.launch {
                val user = databaseService.getUser(player.uniqueId)
                if (word.isBlank()) {
                    user.sendText {
                        error("Das Wort muss mindestens ein Zeichen enthalten.")
                    }
                    return@launch
                }

                val result = denylistService.addToDenylist (
                    word,
                    reason ?: "Kein Grund angegeben.",
                    System.currentTimeMillis(),
                    player.username
                )
                if (result) {
                    user.sendText {
                        success("Du hast das Wort ")
                        variableValue(word)
                        success(" zu der Denylist hinzugefügt.")
                    }
                } else {
                    user.sendText {
                        error("Das Wort ")
                        variableValue(word)
                        error(" ist bereits auf der Denylist.")
                    }
                }
            }
        }
    }
}