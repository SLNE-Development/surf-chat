package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.bukkit.command.argument.channelArgument
import dev.slne.surf.chat.bukkit.command.argument.userArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelMoveCommand() = subcommand("move") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_ADMIN_MOVE)
    userArgument("target")
    channelArgument("channel")
    playerExecutor { player, args ->
        val target: User by args
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

        target.sendText {
            appendPrefix()
            info("Du wurdest in den Nachrichtenkanal ")
            variableValue(channel.channelName)
            info(" verschoben.")
        }
    }
}
