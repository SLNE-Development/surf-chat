package dev.slne.surf.chat.bukkit.command.blacklist

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.textArgument
import dev.slne.surf.chat.bukkit.model.BukkitBlacklistWord
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.blacklistService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class BlacklistAddCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        textArgument("word")
        greedyStringArgument("reason", optional = true)
        playerExecutor { player, args ->
            val word: String by args
            val reason: String? by args

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val result = blacklistService.addToBlacklist(BukkitBlacklistWord(word, reason ?: "Kein Grund angegeben.", System.currentTimeMillis(), player.name))

                if(result) {
                    user.sendText(buildText {
                        primary("Du hast das Wort ")
                        info(word)
                        primary(" zu der Blacklist ")
                        success("hinzugefügt.")
                    })
                } else {
                    user.sendText(buildText {
                        error("Das Wort ")
                        info(word)
                        error(" ist bereits auf der Blacklist.")
                    })
                }
            }
        }
    }
}