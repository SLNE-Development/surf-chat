package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.sendText
import dev.slne.surf.chat.velocity.util.toChannelMember

class ChannelDeleteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_DELETE)
        playerExecutor { player, _ ->
            container.launch {
                val user = databaseService.getUser(player.uniqueId)
                val channel: Channel? = channelService.getChannel(user)

                if (channel == null) {
                    user.sendText {
                        error("Du bist in keinem Nachrichtenkanal.")
                    }
                    return@launch
                }

                val channelMember = user.toChannelMember(channel) ?: run {
                    user.sendText {
                        error("Du bist nicht Mitglied des Nachrichtenkanals ")
                        variableValue(channel.name)
                        error(".")
                    }
                    return@launch
                }

                if (!channel.isOwner(channelMember)) {
                    user.sendText {
                        error("Du bist nicht berechtigt diesen Nachrichtenkanal zu löschen.")
                    }
                    return@launch
                }

                channelService.deleteChannel(channel)
                user.sendText {
                    success("Du hast den Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" gelöscht.")
                }
            }
        }

    }
}
