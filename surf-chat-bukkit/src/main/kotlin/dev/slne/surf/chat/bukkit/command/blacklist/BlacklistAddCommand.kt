package dev.slne.surf.chat.bukkit.command.blacklist

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.textArgument
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.model.BukkitBlacklistWord
import dev.slne.surf.chat.core.service.blacklistService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class BlacklistAddCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        textArgument("word")
        greedyStringArgument("reason", optional = true)
        playerExecutor { player, args ->
            val word: String by args
            val reason: String? by args
            val result = blacklistService.addToBlacklist(BukkitBlacklistWord(word, reason ?: "Kein Grund angegeben.", System.currentTimeMillis(), player.name))

            if(result) {
                surfChatApi.sendText(player, buildText {
                    primary("Du hast das Word ")
                    info(word)
                    primary(" zur Blacklist ")
                    success("hinzugefügt.")
                })
            } else {
                surfChatApi.sendText(player, buildText {
                    error("Das Word ")
                    info(word)
                    error(" ist bereits auf der Blacklist.")
                })
            }
        }
    }
}