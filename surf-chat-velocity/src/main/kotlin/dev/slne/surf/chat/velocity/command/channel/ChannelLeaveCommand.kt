package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.toChannelMember
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

class ChannelLeaveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_LEAVE)
        playerExecutor { player, _ ->
            container.launch {
                val user = databaseService.getUser(player.uniqueId)
                val channel: Channel? = channelService.getChannel(user)

                if(channel == null) {
                    player.sendText {
                        error("Du bist in keinem Nachrichtenkanal.")
                    }
                    return@launch
                }

                channel.handleLeave(user.toChannelMember(channel) ?: run {
                    player.sendText {
                        error("Du bist nicht Mitglied des Nachrichtenkanals ")
                        variableValue(channel.name)
                        error(".")
                    }
                    return@launch
                })
            }
        }
    }
}