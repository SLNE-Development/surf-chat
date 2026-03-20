package dev.slne.surf.chat.paper.command.denylist.action

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.paper.permission.PermissionRegistry

fun denylistActionCommand() = commandAPICommand("denylistaction") {
    withPermission(PermissionRegistry.COMMAND_DENYLIST_ACTION)

    denylistActionAddCommand()
    denylistActionRemoveCommand()
    denylistActionFetchCommand()
    denylistActionListCommand()
    denylistActionClearCommand()
}