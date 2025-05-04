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

                if (channel == null) {
                    user.sendText(buildText {
                        error("Du bist in keinem Nachrichtenkanal.")
                    })

                    return@launch
                }

                if (channel.isOwner(user)) { //if the player who wants to leave is the owner of the channel
                    var nextOwner = channel.getModerators()
                        .firstOrNull() // try to transfer the ownership to the first moderator (in the set) in the channel
                    if (nextOwner == null) {    // if there is no moderator in the channel
                        nextOwner = channel.getMembers()
                            .firstOrNull { it.uuid != user.uuid }   // pick the first channel member (excluded executor (in this case the owner)) because the member set also contains the owner )
                    }
                    if (nextOwner == null) {    // if there is no member in the channel beside the executor, the channel would be empty and can be deleted
                        channel.leave(user)
                        channelService.deleteChannel(channel)
                        user.sendText(buildText {
                            primary("Du hast den Nachrichtenkanal ")
                            info(channel.name)
                            primary(" als letzter Spieler verlassen und der Kanal wurde gelöscht.")
                        })
                        return@launch
                    }
                    channel.transferOwnership(nextOwner)
                    nextOwner.sendText(buildText {
                        info(player.name)
                        primary(" hat den Nachrichtenkanal ")
                        info(channel.name)
                        error(" verlassen. Die Besitzerschaft wurde auf dich übertragen.")
                    })
                }
                channel.leave(user)
                user.sendText(buildText {
                    primary("Du hast den Nachrichtenkanal ")
                    info(channel.name)
                    error(" verlassen.")
                })
            }
        }
    }
}