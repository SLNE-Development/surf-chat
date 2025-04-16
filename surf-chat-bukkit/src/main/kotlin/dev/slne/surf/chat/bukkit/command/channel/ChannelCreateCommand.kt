package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelCreateCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(TextArgument("name"))
        playerExecutor { player, args ->
            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val name: String by args

                if(channelService.getChannel(player) != null) {
                    user.sendText(buildText {
                        error("Du bist bereits in einem Nachrichtenkanal.")
                    })
                    return@launch
                }

                if(channelService.getChannel(name) != null) {
                    user.sendText(buildText {
                        error("Ein Nachrichtenkanal mit dem Namen ")
                        info(name)
                        error(" existiert bereits.")
                    })
                    return@launch
                }

                channelService.createChannel(name, user)
                user.sendText(buildText {
                    primary("Du hast den Nachrichtenkanal ")
                    info(name)
                    success(" erstellt.")
                })
            }
        }
    }
}
