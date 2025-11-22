package dev.slne.surf.chat.paper.command.denylist.action

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.core.client.denylist.denylistActionService
import dev.slne.surf.chat.paper.command.argument.denylistActionArgument
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.denylistActionRemoveCommand() = subcommand("remove") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DENYLIST_ACTION_REMOVE)
    denylistActionArgument("action")
    anyExecutor { executor, args ->
        val action: DenylistAction by args

        if (!denylistActionService.hasAction(action.name)) {
            executor.sendText {
                appendPrefix()
                error("Die Aktion ")
                variableValue(action.name)
                error(" ist nicht in der Aktionsliste vorhanden.")
            }
            return@anyExecutor
        } else {
            denylistActionService.removeAction(action)
        }

        executor.sendText {
            appendPrefix()
            success("Die Aktion ")
            variableValue(action.name)
            success(" wurde erfolgreich aus der Aktionsliste gelöscht.")
        }
    }
}