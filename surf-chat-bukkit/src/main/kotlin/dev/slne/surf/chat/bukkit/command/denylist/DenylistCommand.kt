package dev.slne.surf.chat.bukkit.command.denylist

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.plugin

fun denylistCommand() = commandAPICommand("denylist", plugin) {
    withPermission(PermissionRegistry.COMMAND_DENYLIST)
    denylistAddCommand()
    denylistRemoveCommand()
    denylistFetchCommand()
    denylistListCommand()
    denylistClearCommand()
    denylistImportDefaultCommand()
}