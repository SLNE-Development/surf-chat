package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.bukkit.command.argument.ChannelArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelForceJoinCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.channel.forcejoin")
        withArguments(ChannelArgument("channel"))
        playerExecutor { player, args ->
            val channel = args.getUnchecked<ChannelModel>("channel") ?: return@playerExecutor

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)

                if (channelService.getChannel(player) != null) {
                    user.sendText(buildText {
                        error("Du bist bereits in einem Nachrichtenkanal.")
                    })
                    return@launch
                }

                channel.join(user, true)
                user.sendText(buildText {
                    success("Du bist dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" beigetreten.")
                })
            }
        }
    }
}
