package dev.slne.surf.chat.bukkit.command.denylist

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.textArgument
import dev.slne.surf.chat.bukkit.model.BukkitDenyListEntry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.chat.core.service.databaseService

class DenyListAddCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_DENYLIST_ADD)
        textArgument("word")
        greedyStringArgument("reason", optional = true)
        playerExecutor { player, args ->
            val word: String by args
            val reason: String? by args

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                if (word.isBlank()) {
                    user.sendPrefixed {
                        error("Das Wort muss mindestens ein Zeichen enthalten.")
                    }
                    return@launch
                }

                val result = denylistService.addToDenylist(
                    BukkitDenyListEntry(
                        word,
                        reason ?: "Kein Grund angegeben.",
                        System.currentTimeMillis(),
                        player.name
                    )
                )
                if (result) {
                    user.sendPrefixed {
                        success("Du hast das Wort ")
                        variableValue(word)
                        success(" zu der Denylist hinzugefügt.")
                    }
                } else {
                    user.sendPrefixed {
                        error("Das Wort ")
                        variableValue(word)
                        error(" ist bereits auf der Denylist.")
                    }
                }
            }
        }
    }
}