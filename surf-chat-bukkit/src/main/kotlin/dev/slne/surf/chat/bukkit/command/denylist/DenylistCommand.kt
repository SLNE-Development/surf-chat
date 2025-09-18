package dev.slne.surf.chat.bukkit.command.denylist

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin

fun denylistCommand() = commandAPICommand("denylist", plugin) {
    withPermission(SurfChatPermissionRegistry.COMMAND_DENYLIST)
    denylistAddCommand()
    denylistRemoveCommand()
    denylistFetchCommand()
    denylistListCommand()
}