package dev.slne.surf.chat.bukkit.command.blacklist

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.textArgument
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.core.service.blacklistService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class BlacklistRemoveCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        textArgument("word")
        playerExecutor { player, args ->
            val word: String by args
            val result = blacklistService.removeFromBlacklist(word)

            if(result) {
                surfChatApi.sendText(player, buildText {
                    primary("Du hast das Word ")
                    info(word)
                    primary(" von der Blacklist ")
                    error("entfernt.")
                })
            } else {
                surfChatApi.sendText(player, buildText {
                    error("Das Word ")
                    info(word)
                    error(" ist nicht auf der Blacklist.")
                })
            }
        }
    }
}