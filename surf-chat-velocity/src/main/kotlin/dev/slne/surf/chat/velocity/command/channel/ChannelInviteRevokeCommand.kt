package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.proxy.Player
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.command.argument.playerArgument
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.getUsername
import dev.slne.surf.chat.velocity.util.sendText
import dev.slne.surf.chat.velocity.util.toChannelMember
import dev.slne.surf.chat.velocity.util.toChatUser
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

class ChannelInviteRevokeCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_REVOKE)
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

                val channelMember = user.toChannelMember(channel) ?: run {
                    user.sendText {
                        error("Du bist in diesem Nachrichtenkanal nicht registriert.")
                    }
                    return@launch
                }

                if (!channel.hasModeratorPermissions(channelMember)) {
                    user.sendText {
                        error("Du hast keine Moderationsrechte in diesem Nachrichtenkanal.")
                    }
                    return@launch
                }

                if (!channel.isInvited(targetUser)) {
                    user.sendText {
                        info("Der Spieler ")
                        variableValue(targetUser.getUsername())
                        info(" hat keine Einladung für diesen Nachrichtenkanal.")
                    }
                    return@launch
                }

                channel.revokeInvite(targetUser)

                user.sendText {
                    info("Du hast die Einladung des Spielers ")
                    variableValue(targetUser.getUsername())
                    info(" im Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" zurückgezogen.")
                }

                if (targetUser.channelInvitesEnabled()) {
                    targetUser.sendText {
                        info("Deine Einladung in den Nachrichtenkanal ")
                        variableValue(channel.name)
                        info(" wurde zurückgezogen.")
                    }
                }
            }
        }
    }
}
