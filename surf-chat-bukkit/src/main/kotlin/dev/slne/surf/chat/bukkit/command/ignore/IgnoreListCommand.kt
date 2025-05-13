package dev.slne.surf.chat.bukkit.command.ignore

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.getUsername
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class IgnoreListCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.ignorelist")
        playerExecutor { player, _ ->
            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val ignores = user.ignoreList

                if (ignores.isEmpty()) {
                    surfChatApi.sendText(player, buildText {
                        error("Du ignorierst niemanden.")
                    })
                    return@launch
                }

                val entriesWithNames = ignores.map { entry ->
                    val username = entry.getUsername()
                    entry to username
                }

                user.sendText(buildText {
                    info("Du ignorierst: ")
                    entriesWithNames.forEachIndexed { index, (entry, username) ->
                        if (index > 0) {
                            info(", ")
                        }
                        variableValue(username)
                    }
                })
            }
        }
    }
}