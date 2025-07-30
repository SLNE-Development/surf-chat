package dev.slne.surf.chat.bukkit.command.denylist

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry

fun denylistCommand() = commandAPICommand("denylist") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DENYLIST)
    denylistAddCommand()
    denylistRemoveCommand()
    denylistFetchCommand()
    denylistListCommand()
}