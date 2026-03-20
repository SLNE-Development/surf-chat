package dev.slne.surf.chat.paper.command.denylist

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin

fun denylistCommand() = commandAPICommand("denylist", plugin) {
    withPermission(PermissionRegistry.COMMAND_DENYLIST)
    denylistAddCommand()
    denylistRemoveCommand()
    denylistFetchCommand()
    denylistListCommand()
    denylistClearCommand()
    denylistImportDefaultCommand()
}