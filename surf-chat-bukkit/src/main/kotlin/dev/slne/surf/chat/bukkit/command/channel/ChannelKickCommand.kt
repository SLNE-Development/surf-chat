package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.bukkit.command.argument.ChannelMembersArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService

import org.bukkit.entity.Player

class ChannelKickCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_KICK)
        withArguments(ChannelMembersArgument("player"))
        playerExecutor { player, args ->
            val channel: Channel? = channelService.getChannel(player)
            val target = args.getUnchecked<Player>("player") ?: return@playerExecutor

            if(channel == null) {
                player.sendPrefixed {
                    error("Du bist in keinem Nachrichtenkanal.")
                }
                return@playerExecutor
            }

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                if (!channel.hasModeratorPermissions(user)) {
                    user.sendPrefixed {
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    }
                    return@launch
                }

                if (channel.hasModeratorPermissions(targetUser) && channel.hasModeratorPermissions(user)) {
                    user.sendPrefixed {
                        error("Du kannst keine Spieler aus diesem Kanal entfernen.")
                    }
                    return@launch
                }

                channel.kick(targetUser)
                user.sendPrefixed {
                    info("Du hast ")
                    variableValue(targetUser.getName())
                    info(" aus dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" geworfen.")
                }

                targetUser.sendPrefixed {
                    info("Du wurdest aus dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" geworfen.")
                }
            }
        }
    }
}
