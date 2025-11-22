package dev.slne.surf.chat.paper.command.denylist

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.plugin

fun denylistCommand() = commandAPICommand("denylist", plugin) {
    withPermission(SurfChatPermissionRegistry.COMMAND_DENYLIST)
    denylistAddCommand()
    denylistRemoveCommand()
    denylistListCommand()
    denylistClearCommand()
    denylistImportDefaultCommand()
}