package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin

fun channelAdminCommand() = commandAPICommand("channeladmin", plugin) {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_ADMIN)

    channelForceJoinCommand()
    channelForceDeleteCommand()
    channelSpyCommand()
    channelMoveCommand()
}
