package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.proxy.Player
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelRole
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.command.argument.playerArgument
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.sendText
import dev.slne.surf.chat.velocity.util.toChannelMember
import dev.slne.surf.chat.velocity.util.toChatUser

class ChannelDemoteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_DEMOTE)
        playerArgument("player")
        playerExecutor { player, args ->

            container.launch {
                val target = args.getUnchecked<Player>("player") ?: return@launch

                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                val channel: Channel? = channelService.getChannel(user)

                if (channel == null) {
                    user.sendText {
                        error("Du bist in keinem Nachrichtenkanal.")
                    }
                    return@launch
                }

                val userChannelMember = user.toChannelMember(channel) ?: run {
                    user.sendText {
                        error("Du bist kein Mitglied in dem Nachrichtenkanal.")
                    }
                    return@launch
                }

                val targetChannelMember = targetUser.toChannelMember(channel) ?: run {
                    user.sendText {
                        error("Der Spieler ")
                        variableValue(target.username)
                        error(" ist kein Mitglied in dem Nachrichtenkanal.")
                    }
                    return@launch
                }

                if (target.uniqueId == player.uniqueId) {
                    user.sendText {
                        error("Du kannst dich nicht selbst degradieren.")
                    }
                    return@launch
                }

                if (!channel.isOwner(userChannelMember)) {
                    user.sendText {
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    }
                    return@launch
                }

                if (!channel.isMember(targetUser)) {
                    user.sendText {
                        error("Der Spieler ")
                        variableValue(target.username)
                        error(" ist kein Mitglied in dem Nachrichtenkanal.")
                    }
                    return@launch
                }

                if (!channel.hasModeratorPermissions(targetChannelMember) && !channel.isOwner(targetChannelMember)) {
                    user.sendText {
                        error("Der Spieler ")
                        variableValue(target.username)
                        error(" ist bereits ein Mitglied.")
                    }
                    return@launch
                }

                if (channel.members.none { it.role == ChannelRole.OWNER }) {
                    user.sendText {
                        error("Der Nachrichtenkanal benötigt mindestens einen Besitzer.")
                    }
                    return@launch
                }

                channel.demote(targetChannelMember)

                user.sendText {
                    success("Du hast ")
                    variableValue(target.username)
                    success(" degradiert.")
                }

                targetUser.sendText {
                    info("Du wurdest degradiert.")
                }
            }
        }
    }
}
