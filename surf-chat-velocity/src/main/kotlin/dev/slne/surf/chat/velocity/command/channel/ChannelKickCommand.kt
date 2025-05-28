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
import dev.slne.surf.chat.velocity.util.getUsername
import dev.slne.surf.chat.velocity.util.sendText
import dev.slne.surf.chat.velocity.util.toChannelMember
import dev.slne.surf.chat.velocity.util.toChatUser
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

class ChannelKickCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_KICK)
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

                val targetChannelMember = targetUser.toChannelMember(channel) ?: run {
                    user.sendText {
                        error("Du bist in diesem Nachrichtenkanal nicht registriert.")
                    }
                    return@launch
                }

                if (!channel.hasModeratorPermissions(channelMember)) {
                    user.sendText {
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    }
                    return@launch
                }

                if (channel.hasModeratorPermissions(targetChannelMember) && channel.hasModeratorPermissions(channelMember)) {
                    user.sendText {
                        error("Du kannst keine Spieler aus diesem Kanal entfernen.")
                    }
                    return@launch
                }

                channel.kick(targetChannelMember)
                user.sendText {
                    info("Du hast ")
                    variableValue(targetUser.getUsername())
                    info(" aus dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" geworfen.")
                }

                targetUser.sendText {
                    info("Du wurdest aus dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" geworfen.")
                }
            }
        }
    }
}
