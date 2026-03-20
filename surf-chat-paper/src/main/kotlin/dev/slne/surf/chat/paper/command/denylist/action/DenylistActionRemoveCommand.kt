package dev.slne.surf.chat.paper.command.denylist.action

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.paper.command.argument.denylistActionArgument
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.core.service.denylistActionService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.denylistActionRemoveCommand() = subcommand("remove") {
    withPermission(PermissionRegistry.COMMAND_DENYLIST_ACTION_REMOVE)
    denylistActionArgument("action")
    anyExecutor { executor, args ->
        val action: DenylistAction by args

        if (!denylistActionService.hasLocalAction(action.name)) {
            executor.sendText {
                appendErrorPrefix()
                error("Die Aktion ")
                variableValue(action.name)
                error(" ist nicht in der internen Aktionsliste vorhanden.")
            }
            return@anyExecutor
        } else {
            denylistActionService.removeLocalAction(action)
        }

        executor.sendText {
            appendSuccessPrefix()
            success("Die Aktion ")
            variableValue(action.name)
            success(" wurde erfolgreich aus der internen Aktionsliste gelöscht.")
        }

        plugin.launch {
            if (!denylistActionService.hasAction(action.name)) {
                executor.sendText {
                    appendErrorPrefix()
                    error("Die Aktion ")
                    variableValue(action.name)
                    error(" ist nicht in der externen Aktionsliste vorhanden.")
                }
                return@launch
            }

            denylistActionService.removeAction(action)

            executor.sendText {
                appendSuccessPrefix()
                success("Die Aktion ")
                variableValue(action.name)
                success(" wurde erfolgreich aus der externen Aktionsliste gelöscht.")
            }
        }
    }
}