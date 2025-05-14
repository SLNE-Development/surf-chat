package dev.slne.surf.chat.bukkit.command.blacklist

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.blacklistService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import kotlin.system.measureTimeMillis

class BlacklistUpdateCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        playerExecutor { player, _ ->
            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val duration = measureTimeMillis {
                    blacklistService.fetch()
                }

                user.sendText(buildText {
                    success("Die Blacklist wurde aktualisiert. ")
                    info("(${duration}ms)")
                })
            }
        }
    }
}