package dev.slne.surf.chat.bukkit.command.denylist.action

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry

fun denylistActionCommand() = commandAPICommand("denylist") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DENYLIST_ACTION)

    denylistActionAddCommand()
    denylistActionRemoveCommand()
    denylistActionFetchCommand()
    denylistActionListCommand()
}