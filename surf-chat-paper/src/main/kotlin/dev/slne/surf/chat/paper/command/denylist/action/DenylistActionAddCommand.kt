package dev.slne.surf.chat.paper.command.denylist.action

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistActionType
import dev.slne.surf.chat.core.client.denylist.denylistActionService
import dev.slne.surf.chat.paper.command.argument.denylistActionTypeArgument
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.denylistActionAddCommand() = subcommand("add") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DENYLIST_ACTION_ADD)
    stringArgument("name")
    denylistActionTypeArgument("type")
    integerArgument("durationInMinutes", 0)
    greedyStringArgument("reason")
    anyExecutor { executor, args ->
        val name: String by args
        val type: DenylistActionType by args
        val reason: String by args
        val durationInMinutes: Int by args

        if (denylistActionService.hasAction(name)) {
            executor.sendText {
                appendPrefix()
                error("Die Aktion ")
                variableValue(name)
                error(" ist bereits in der Aktionsliste vorhanden.")
            }
            return@anyExecutor
        } else {
            denylistActionService.addAction(
                DenylistAction(
                    name,
                    type,
                    reason,
                    durationInMinutes * 60 * 1000L
                )
            )
        }

        executor.sendText {
            appendPrefix()
            success("Die Aktion ")
            variableValue(name)
            success(" wurde erfolgreich zur Aktionsliste hinzugefügt.")
        }
    }
}