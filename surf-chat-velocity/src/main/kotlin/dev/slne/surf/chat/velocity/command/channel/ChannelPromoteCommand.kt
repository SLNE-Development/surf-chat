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
import dev.slne.surf.chat.velocity.util.sendText
import dev.slne.surf.chat.velocity.util.toChannelMember
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

class ChannelPromoteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_PROMOTE)
        playerArgument("player")
        playerExecutor { player, args ->
            container.launch {
                val user = databaseService.getUser(player.uniqueId)
                val channel: Channel? = channelService.getChannel(user)
                val target = args.getUnchecked<Player>("player") ?: return@launch

                if(channel == null) {
                    player.sendText {
                        error("Du bist in keinem Nachrichtenkanal.")
                    }
                    return@launch
                }

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

                if (!channel.isOwner(channelMember)) {
                    user.sendText {
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    }
                    return@launch
                }

                if (channel.hasModeratorPermissions(targetChannelMember)) {
                    user.sendText {
                        error("Der Spieler ")
                        variableValue(target.username ?: target.uniqueId.toString())
                        error(" ist bereits Moderator.")
                    }
                    return@launch
                }

                channel.promote(targetChannelMember)

                user.sendText {
                    success("Du hast ")
                    variableValue(target.username ?: target.uniqueId.toString())
                    success(" befördert.")
                }

                targetUser.sendText {
                    info("Du wurdest befördert.")
                }
            }
        }
    }
}
