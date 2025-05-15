package dev.slne.surf.chat.bukkit.command.ignore

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.getUsername
import dev.slne.surf.chat.bukkit.util.utils.sendText
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.util.*

class IgnoreListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_IGNORE_LIST)
        playerExecutor { player, _ ->
            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val ignores: ObjectSet<UUID> = user.ignoreList

                if (ignores.isEmpty()) {
                    surfChatApi.sendText(player, buildText {
                        error("Du ignorierst niemanden.")
                    })
                    return@launch
                }

                val entriesWithNames = ignores.map {
                    async {
                        it to it.getUsername()
                    }
                }.awaitAll()

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