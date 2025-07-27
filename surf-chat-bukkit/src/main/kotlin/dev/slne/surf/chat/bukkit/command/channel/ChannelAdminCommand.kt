package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry

fun channelAdminCommand() = commandAPICommand("channeladmin") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_ADMIN)

    subcommand(ChannelMoveCommand("move"))
    subcommand(ChannelForceDeleteCommand("forceDelete"))
    subcommand(ChannelForceJoinCommand("forceJoin"))
    subcommand(ChannelSpyCommand("spy"))
}
