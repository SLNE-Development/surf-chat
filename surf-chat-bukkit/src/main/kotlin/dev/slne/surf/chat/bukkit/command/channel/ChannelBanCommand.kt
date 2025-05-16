package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.bukkit.command.argument.ChannelMembersArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import org.bukkit.entity.Player

class ChannelBanCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelMembersArgument("player"))
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_BAN)
        playerExecutor { player, args ->
            val channel: ChannelModel? = channelService.getChannel(player)
            val target = args.getUnchecked<Player>("player") ?: return@playerExecutor

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                if (channel == null) {
                    user.sendPrefixed {
                        error("Du bist in keinem Nachrichtenkanal.")
                    }
                    return@launch
                }

                if (!channel.hasModeratorPermissions(user)) {
                    user.sendPrefixed {
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    }
                    return@launch
                }

                if (channel.hasModeratorPermissions(targetUser) && channel.hasModeratorPermissions(user)) {
                    user.sendPrefixed {
                        error("Du kannst diesen Spieler nicht entfernen.")
                    }
                    return@launch
                }

                channel.ban(targetUser)

                user.sendPrefixed {
                    success("Du hast ")
                    variableValue(target.name)
                    success(" aus dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" verbannt.")
                }
                targetUser.sendPrefixed {
                    info("Du wurdest aus dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" verbannt.")
                }
            }
        }
    }
}
