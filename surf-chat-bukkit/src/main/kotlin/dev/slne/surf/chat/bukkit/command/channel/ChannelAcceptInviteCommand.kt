package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.bukkit.command.argument.ChannelInviteArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.databaseService

class ChannelAcceptInviteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelInviteArgument("channel"))
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_ACCEPT)

        playerExecutor { player, args ->
            val channel = args.getUnchecked<Channel>("channel") ?: return@playerExecutor

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)


                if (!channel.isInvited(user)) {
                    user.sendPrefixed {
                        error("Du hast keine Einladung für den Nachrichtenkanal ")
                        variableValue(channel.name)
                        error(" erhalten.")
                    }
                    return@launch
                }

                user.acceptInvite(channel)
                user.sendPrefixed {
                    info("Du hast die Einladung für den Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" angenommen.")
                }
            }
        }
    }
}
