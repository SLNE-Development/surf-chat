package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

class ChannelInviteRevokeCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_REVOKE)
        withArguments(EntitySelectorArgument.OnePlayer("player").replaceSuggestions(
            ArgumentSuggestions.stringCollection {
                val players = Bukkit.getOnlinePlayers().map { it.name }
                players.toSet()
            }))
        playerExecutor { player, args ->
            val channel: Channel? = channelService.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@playerExecutor

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
                        error("Du hast keine Moderationsrechte in diesem Nachrichtenkanal.")
                    }
                    return@launch
                }

                if (!channel.isInvited(targetUser)) {
                    user.sendPrefixed {
                        info("Der Spieler ")
                        variableValue(targetUser.getName())
                        info(" hat keine Einladung für diesen Nachrichtenkanal.")
                    }
                    return@launch
                }

                channel.revokeInvite(targetUser)

                user.sendPrefixed {
                    info("Du hast die Einladung des Spielers ")
                    variableValue(targetUser.getName())
                    info(" im Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" zurückgezogen.")
                }

                if (targetUser.channelInvites) {
                    targetUser.sendPrefixed {
                        info("Deine Einladung in den Nachrichtenkanal ")
                        variableValue(channel.name)
                        info(" wurde zurückgezogen.")
                    }
                }
            }
        }
    }
}
