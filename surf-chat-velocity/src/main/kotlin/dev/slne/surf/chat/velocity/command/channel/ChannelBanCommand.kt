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
import dev.slne.surf.chat.velocity.util.toChatUser

class ChannelBanCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        playerArgument("player")
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_BAN)
        playerExecutor { player, args ->
            container.launch {
                val channel: Channel? = channelService.getChannel(player.toChatUser())
                val target = args.getUnchecked<Player>("player") ?: return@launch
                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                if (channel == null) {
                    user.sendText {
                        error("Du bist in keinem Nachrichtenkanal.")
                    }
                    return@launch
                }

                val channelUser = user.toChannelMember(channel) ?: run {
                    user.sendText {
                        error("Du bist in diesem Nachrichtenkanal nicht registriert.")
                    }
                    return@launch
                }
                val channelTarget = targetUser.toChannelMember(channel) ?: run {
                    user.sendText {
                        error("Dieser Spieler ist in diesem Nachrichtenkanal nicht registriert.")
                    }
                    return@launch
                }

                if (!channel.hasModeratorPermissions(channelUser)) {
                    user.sendText {
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    }
                    return@launch
                }

                if (channel.hasModeratorPermissions(channelTarget) && channel.hasModeratorPermissions(channelUser)) {
                    user.sendText {
                        error("Du kannst diesen Spieler nicht entfernen.")
                    }
                    return@launch
                }

                channel.ban(targetUser)

                user.sendText {
                    success("Du hast ")
                    variableValue(target.username)
                    success(" aus dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" verbannt.")
                }
                targetUser.sendText {
                    info("Du wurdest aus dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" verbannt.")
                }
            }
        }
    }
}
