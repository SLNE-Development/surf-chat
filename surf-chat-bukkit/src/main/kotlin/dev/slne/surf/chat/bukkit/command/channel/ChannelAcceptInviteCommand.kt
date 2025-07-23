package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.databaseService

fun CommandAPICommand.inviteAcceptCommand() = subcommand("invite") {
    init {
        withArguments(ChannelInviteArgument("channel"))
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_ACCEPT)

        playerExecutor { player, args ->
            val channel = args.getUnchecked<ChannelModel>("channel") ?: return@playerExecutor

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