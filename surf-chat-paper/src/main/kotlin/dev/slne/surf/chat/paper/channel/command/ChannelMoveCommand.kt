package dev.slne.surf.chat.paper.channel.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.paper.channel.argument.channelArgument
import dev.slne.surf.chat.paper.channel.channelService
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.util.sendText
import dev.slne.surf.cloud.api.client.paper.command.args.onlineCloudPlayerArgument
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelMoveCommand() = subcommand("move") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_ADMIN_MOVE)
    onlineCloudPlayerArgument("target")
    channelArgument("channel")
    playerExecutor { player, args ->
        val target: CloudPlayer by args
        val channel: Channel by args

        channelService.move(target, channel)

        player.sendText {
            appendPrefix()
            success("Du hast ")
            variableValue(target.name)
            success(" in den Nachrichtenkanal ")
            variableValue(channel.channelName)
            success(" verschoben.")
        }

        channel.sendText {
            appendPrefix()
            variableValue(target.name)
            info(" hat den Nachrichtenkanal betreten.")
        }

        target.sendText {
            appendPrefix()
            info("Du wurdest in den Nachrichtenkanal ")
            variableValue(channel.channelName)
            info(" verschoben.")
        }
    }
}
