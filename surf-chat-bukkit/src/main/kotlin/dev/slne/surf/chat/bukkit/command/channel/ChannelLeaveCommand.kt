package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelLeaveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_LEAVE)
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

                if (channel.isOwner(user)) {
                    var nextOwner = channel.getModerators().firstOrNull()

                    if (nextOwner == null) {
                        nextOwner = channel.getMembers(false).firstOrNull { it.uuid != user.uuid }
                    }

                    if (nextOwner == null) {
                        channel.leave(user)
                        channelService.deleteChannel(channel)
                        user.sendText(buildText {
                            info("Du hast den Nachrichtenkanal ")
                            variableValue(channel.name)
                            info(" als letzter Spieler verlassen und der Kanal wurde gelöscht.")
                        })
                        return@launch
                    }
                    channel.transferOwnership(nextOwner)
                    nextOwner.sendText(buildText {
                        variableValue(player.name)
                        info(" hat den Nachrichtenkanal ")
                        variableValue(channel.name)
                        info(" verlassen. Die Besitzerschaft wurde auf dich übertragen.")
                    })
                }

                channel.leave(user)
                user.sendText(buildText {
                    success("Du hast den Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" verlassen.")
                })
            }
        }
    }
}