package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelStatus
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.command.argument.channelArgument
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.sendText
import dev.slne.surf.chat.velocity.util.toChatUser

class ChannelJoinCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_JOIN)
        channelArgument("channel")
        playerExecutor { player, args ->
            val channel = args.getUnchecked<Channel>("channel") ?: return@playerExecutor

            container.launch {
                val user = databaseService.getUser(player.uniqueId)

                if (channelService.getChannel(user) != null) {
                    user.sendText {
                        error("Du bist bereits in einem Nachrichtenkanal.")
                    }
                    return@launch
                }

                if (channel.isBanned(user)) {
                    user.sendText {
                        error("Du wurdest von diesem Nachrichtenkanal ausgeschlossen.")
                    }
                    return@launch
                }

                if (channel.status != ChannelStatus.PUBLIC && !channel.isInvited(user)) {
                    user.sendText {
                        error("Der Nachrichtenkanal ")
                        variableValue(channel.name)
                        error(" ist privat.")
                    }
                    return@launch
                }

                channel.join(user)
                user.sendText {
                    info("Du bist dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" beigetreten.")
                }
            }
        }
    }
}
