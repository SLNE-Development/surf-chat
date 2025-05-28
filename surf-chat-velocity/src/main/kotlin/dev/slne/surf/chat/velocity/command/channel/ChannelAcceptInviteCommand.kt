package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.command.argument.channelArgument
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.sendText

class ChannelAcceptInviteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        channelArgument("channel")
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_ACCEPT)

        playerExecutor { player, args ->
            val channel = args.getUnchecked<Channel>("channel") ?: return@playerExecutor

            container.launch {
                val user = databaseService.getUser(player.uniqueId)


                if (!channel.isInvited(user)) {
                    user.sendText {
                        error("Du hast keine Einladung für den Nachrichtenkanal ")
                        variableValue(channel.name)
                        error(" erhalten.")
                    }
                    return@launch
                }

                user.acceptInvite(channel)
                user.sendText {
                    info("Du hast die Einladung für den Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" angenommen.")
                }
            }
        }
    }
}
