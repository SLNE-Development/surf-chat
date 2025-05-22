package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.bukkit.command.argument.ChannelArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService

class ChannelForceJoinCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_ADMIN_JOIN)
        withArguments(ChannelArgument("channel"))
        playerExecutor { player, args ->
            val channel = args.getUnchecked<Channel>("channel") ?: return@playerExecutor

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)

                if (channelService.getChannel(player) != null) {
                    user.sendPrefixed {
                        error("Du bist bereits in einem Nachrichtenkanal.")
                    }
                    return@launch
                }

                channel.join(user, true)
                user.sendPrefixed {
                    success("Du bist dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" beigetreten.")
                }
            }
        }
    }
}
