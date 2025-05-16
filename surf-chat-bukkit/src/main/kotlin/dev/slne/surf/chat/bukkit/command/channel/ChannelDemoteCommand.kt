package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.type.ChannelRoleType
import dev.slne.surf.chat.bukkit.command.argument.ChannelMembersArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import org.bukkit.entity.Player

class ChannelDemoteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_DEMOTE)
        withArguments(ChannelMembersArgument("player"))
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

                if (target.uniqueId == player.uniqueId) {
                    user.sendPrefixed {
                        error("Du kannst dich nicht selbst degradieren.")
                    }
                    return@launch
                }

                if (!channel.isOwner(user)) {
                    user.sendPrefixed {
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    }
                    return@launch
                }

                if (!channel.isMember(targetUser)) {
                    user.sendPrefixed {
                        error("Der Spieler ")
                        variableValue(target.name)
                        error(" ist kein Mitglied in dem Nachrichtenkanal.")
                    }
                    return@launch
                }

                if (!channel.hasModeratorPermissions(targetUser) && !channel.isOwner(targetUser)) {
                    user.sendPrefixed {
                        error("Der Spieler ")
                        variableValue(target.name)
                        error(" ist bereits ein Mitglied.")
                    }
                    return@launch
                }

                if (channel.members.filter { it.value == ChannelRoleType.OWNER }.isEmpty()) {
                    user.sendPrefixed {
                        error("Der Nachrichtenkanal benötigt mindestens einen Besitzer.")
                    }
                    return@launch
                }

                channel.demote(targetUser)

                user.sendPrefixed {
                    success("Du hast ")
                    variableValue(target.name)
                    success(" degradiert.")
                }

                targetUser.sendPrefixed {
                    info("Du wurdest degradiert.")
                }
            }
        }
    }
}
