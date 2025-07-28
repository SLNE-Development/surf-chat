package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.components
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer

fun CommandAPICommand.channelInviteCommand() = subcommand("invite") {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_INVITE)
        withArguments(
            EntitySelectorArgument.OnePlayer("player").replaceSuggestions(
                ArgumentSuggestions.stringCollection {
                    val players = Bukkit.getOnlinePlayers().map { it.name }
                    players.toSet()
                })
        )
        playerExecutor { player, args ->
            val channel: ChannelModel? = channelService.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@playerExecutor

            if (channel == null) {
                player.sendPrefixed {
                    error("Du bist in keinem Nachrichtenkanal.")
                }
                return@playerExecutor
            }

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                if (channel.isInvited(targetUser)) {
                    user.sendPrefixed {
                        error("Der Spieler ")
                        variableValue(target.name ?: target.uniqueId.toString())
                        error(" wurde bereits eingeladen.")
                    }
                    return@launch
                }

                if (channel.isMember(targetUser)) {
                    user.sendPrefixed {
                        error("Der Spieler ")
                        variableValue(target.name ?: target.uniqueId.toString())
                        error(" ist bereits in diesem Nachrichtenkanal.")
                    }
                    return@launch
                }

                if (!channel.hasModeratorPermissions(user)) {
                    user.sendPrefixed {
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    }
                    return@launch
                }

                channel.invite(targetUser)

                user.sendPrefixed {
                    info("Du hast ")
                    variableValue(target.name ?: target.uniqueId.toString())
                    info(" in den Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" eingeladen.")
                }

                if (targetUser.channelInvites) {
                    targetUser.sendPrefixed {
                        info("Du wurdest in den Nachrichtenkanal ")
                        variableValue(channel.name)
                        info(" eingeladen. ")

                        append(components.getInviteAcceptComponent(channel))
                        append(components.getInviteDeclineComponent(channel))
                    }
                }
            }
        }
    }
}
