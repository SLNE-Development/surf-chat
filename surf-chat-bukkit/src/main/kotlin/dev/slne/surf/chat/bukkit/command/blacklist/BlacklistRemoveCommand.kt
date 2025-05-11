package dev.slne.surf.chat.bukkit.command.blacklist

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.textArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.blacklistService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class BlacklistRemoveCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        textArgument("word")
        playerExecutor { player, args ->
            val word: String by args
            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val result = blacklistService.removeFromBlacklist(word)

                if(result) {
                    user.sendText(buildText {
                        success("Du hast das Wort ")
                        variableValue(word)
                        success(" von der Blacklist entfernt.")
                    })
                } else {
                    user.sendText(buildText {
                        error("Das Wort ")
                        variableValue(word)
                        error(" befindet sich nicht auf der Blacklist.")
                    })
                }
            }
        }
    }
}