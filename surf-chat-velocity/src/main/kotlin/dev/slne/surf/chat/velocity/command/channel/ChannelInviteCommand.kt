package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.proxy.Player
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.command.argument.playerArgument
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.components
import dev.slne.surf.chat.velocity.util.sendText
import dev.slne.surf.chat.velocity.util.toChannelMember
import dev.slne.surf.chat.velocity.util.toChatUser
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

class ChannelInviteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_INVITE)
        playerArgument("player")
        playerExecutor { player, args ->
            container.launch {
                val channel: Channel? = channelService.getChannel(player.toChatUser())
                val target = args.getUnchecked<Player>("player") ?: return@launch

                if(channel == null) {
                    player.sendText {
                        error("Du bist in keinem Nachrichtenkanal.")
                    }
                    return@launch
                }

                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                val channelMember = targetUser.toChannelMember(channel) ?: run {
                    user.sendText {
                        error("Der Spieler ")
                        variableValue(target.username ?: target.uniqueId.toString())
                        error(" ist in diesem Nachrichtenkanal nicht registriert.")
                    }
                    return@launch
                }

                if (channel.isInvited(targetUser)) {
                    user.sendText {
                        error("Der Spieler ")
                        variableValue(target.username ?: target.uniqueId.toString())
                        error(" wurde bereits eingeladen.")
                    }
                    return@launch
                }

                if (channel.isMember(targetUser)) {
                    user.sendText {
                        error("Der Spieler ")
                        variableValue(target.username ?: target.uniqueId.toString())
                        error(" ist bereits in diesem Nachrichtenkanal.")
                    }
                    return@launch
                }

                if (!channel.hasModeratorPermissions(channelMember)) {
                    user.sendText {
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    }
                    return@launch
                }

                channel.invite(targetUser)

                user.sendText {
                    info("Du hast ")
                    variableValue(target.username ?: target.uniqueId.toString())
                    info(" in den Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" eingeladen.")
                }

                if (targetUser.channelInvitesEnabled()) {
                    targetUser.sendText {
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
