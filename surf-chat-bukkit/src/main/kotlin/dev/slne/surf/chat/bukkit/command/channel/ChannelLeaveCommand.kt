package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelLeaveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        playerExecutor { player, _ ->
            val channel: ChannelModel? = channelService.getChannel(player)

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)

                if(channel == null) {
                    user.sendText(buildText {
                        error("Du bist in keinem Nachrichtenkanal.")
                    })

                    return@launch
                }

                if(channel.isOwner(user)) {
                    val mayBeNextOwner = channel.getMembers()
                        .filter { it.uuid != user.uuid }
                        .sortedWith(compareBy(
                            { if (channel.isModerator(it)) 0 else 1 },
                            { channel.members[it] }
                        ))
                        .firstOrNull()

                    if(mayBeNextOwner == null) {
                        channelService.deleteChannel(channel)
                    } else {
                        channel.transferOwnership(mayBeNextOwner)
                        channel.leave(user)
                    }
                } else {
                    channel.leave(user)
                }

                user.sendText(buildText {
                    primary("Du hast den Nachrichtenkanal ")
                    info(channel.name)
                    error(" verlassen.")
                })
            }
        }
    }
}
