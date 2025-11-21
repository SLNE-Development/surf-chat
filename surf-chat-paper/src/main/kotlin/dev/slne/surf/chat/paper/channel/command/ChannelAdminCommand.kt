package dev.slne.surf.chat.paper.channel.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.plugin

fun channelAdminCommand() = commandAPICommand("channeladmin", plugin) {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_ADMIN)

    channelForceJoinCommand()
    channelForceDeleteCommand()
    channelSpyCommand()
    channelMoveCommand()
}
