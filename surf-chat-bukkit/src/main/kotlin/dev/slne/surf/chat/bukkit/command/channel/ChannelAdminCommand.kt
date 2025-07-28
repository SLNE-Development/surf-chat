package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry

fun channelAdminCommand() = commandAPICommand("channeladmin") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_ADMIN)

    channelForceJoinCommand()
    channelForceDeleteCommand()
    channelSpyCommand()
    channelMoveCommand()
}
