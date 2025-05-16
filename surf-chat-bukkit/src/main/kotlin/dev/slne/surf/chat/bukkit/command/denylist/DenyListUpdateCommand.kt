package dev.slne.surf.chat.bukkit.command.denylist

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.chat.core.service.databaseService
import kotlin.system.measureTimeMillis

class DenyListUpdateCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_DENYLIST_UPDATE)
        playerExecutor { player, _ ->
            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val duration = measureTimeMillis {
                    denylistService.fetch()
                }

                user.sendPrefixed {
                    success("Die Denylist wurde aktualisiert. ")
                    info("(${duration}ms)")
                }
            }
        }
    }
}