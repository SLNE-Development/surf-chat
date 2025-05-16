package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.type.ChannelStatusType
import dev.slne.surf.chat.bukkit.command.argument.ChannelArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService

class ChannelJoinCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_JOIN)
        withArguments(ChannelArgument("channel"))
        playerExecutor { player, args ->
            val channel = args.getUnchecked<ChannelModel>("channel") ?: return@playerExecutor

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)

                if (channelService.getChannel(player) != null) {
                    user.sendPrefixed {
                        error("Du bist bereits in einem Nachrichtenkanal.")
                    }
                    return@launch
                }

                if (channel.isBanned(user)) {
                    user.sendPrefixed {
                        error("Du wurdest von diesem Nachrichtenkanal ausgeschlossen.")
                    }
                    return@launch
                }

                if (channel.status != ChannelStatusType.PUBLIC && !channel.isInvited(user)) {
                    user.sendPrefixed {
                        error("Der Nachrichtenkanal ")
                        info(channel.name)
                        error(" ist privat.")
                    }
                    return@launch
                }

                channel.join(user)
                user.sendPrefixed {
                    info("Du bist dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" beigetreten.")
                }
            }
        }
    }
}
