package dev.slne.surf.chat.velocity.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.velocity.command.argument.channelArgument
import dev.slne.surf.chat.velocity.util.ChatPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

class ChannelForceDeleteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_ADMIN_DELETE)
        channelArgument("channel")
        playerExecutor { player, args ->
            val channel = args.getUnchecked<Channel>("channel") ?: return@playerExecutor

            channelService.deleteChannel(channel)

            player.sendText {
                success("Der Nachrichtenkanal ")
                variableValue(channel.name)
                success(" wurde gelöscht.")
            }
        }
    }
}
