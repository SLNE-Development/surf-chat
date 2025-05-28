package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.proxy.Player
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.command.argument.channelArgument
import dev.slne.surf.chat.velocity.command.argument.playerArgument
import dev.slne.surf.chat.velocity.container
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.chat.velocity.util.getUsername
import dev.slne.surf.chat.velocity.util.sendText

class ChannelMoveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_ADMIN_MOVE)
        playerArgument("player")
        channelArgument("channel")
        withPermission("surf.chat.command.channel.move")

        playerExecutor { player, args ->
            val target = args.getUnchecked<Player>("player") ?: return@playerExecutor
            val channel = args.getUnchecked<Channel>("channel") ?: return@playerExecutor

            container.launch {
                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                channelService.move(targetUser, channel)

                user.sendText {
                    success("Du hast ")
                    variableValue(targetUser.getUsername())
                    success(" in den Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" verschoben.")
                }

                targetUser.sendText {
                    info("Du wurdest in den Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" verschoben.")
                }
            }
        }
    }
}
