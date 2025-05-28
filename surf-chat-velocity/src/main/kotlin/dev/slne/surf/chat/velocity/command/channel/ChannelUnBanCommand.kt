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

class ChannelUnBanCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_UNBAN)
        playerArgument("player")
        playerExecutor { player, args ->
            container.launch {
                val target = args.getUnchecked<Player>("player") ?: return@launch
                val user = databaseService.getUser(player.uniqueId)
                val channel: Channel? = channelService.getChannel(user)

                if(channel == null) {
                    user.sendText {
                        error("Du bist in keinem Nachrichtenkanal.")
                    }
                    return@launch
                }

                val targetUser = databaseService.getUser(target.uniqueId)

                if (!channel.hasModeratorPermissions(user.toChannelMember(channel) ?: return@launch)) {
                    user.sendText {
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    }
                    return@launch
                }

                if (!channel.isBanned(targetUser)) {
                    user.sendText {
                        error("Der Spieler ")
                        variableValue(targetUser.getUsername())
                        error(" ist nicht im Nachrichtenkanal gebannt.")
                    }
                    return@launch
                }

                channel.unban(targetUser)

                user.sendText {
                    success("Du hast den Spieler ")
                    variableValue(target.username)
                    success(" im Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" entbannt.")
                }

                targetUser.sendText {
                    info("Du wurdest im Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" entbannt.")
                }
            }
        }
    }
}
