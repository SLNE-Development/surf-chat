package dev.slne.surf.chat.bukkit.command.channel

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
import org.bukkit.entity.Player

class ChannelUnBanCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_UNBAN)
        withArguments(EntitySelectorArgument.OnePlayer("player").replaceSuggestions(
            ArgumentSuggestions.stringCollection {
                val players = Bukkit.getOnlinePlayers().map { it.name }
                players.toSet()
            }))
        playerExecutor { player, args ->
            val target = args.getUnchecked<Player>("player") ?: return@playerExecutor
            val channel: Channel? = channelService.getChannel(player)

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

                if (!channel.isBanned(targetUser)) {
                    user.sendPrefixed {
                        error("Der Spieler ")
                        variableValue(targetUser.getName())
                        error(" ist nicht im Nachrichtenkanal gebannt.")
                    }
                    return@launch
                }

                channel.unban(targetUser)

                user.sendPrefixed {
                    success("Du hast den Spieler ")
                    variableValue(target.name)
                    success(" im Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" entbannt.")
                }

                targetUser.sendPrefixed {
                    info("Du wurdest im Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" entbannt.")
                }
            }
        }
    }
}
