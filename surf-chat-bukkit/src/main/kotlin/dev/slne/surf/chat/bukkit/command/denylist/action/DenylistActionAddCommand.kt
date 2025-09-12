package dev.slne.surf.chat.bukkit.command.denylist.action

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.chat.api.entry.DenylistActionType
import dev.slne.surf.chat.bukkit.command.argument.denylistActionTypeArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.entry.DenylistActionImpl
import dev.slne.surf.chat.core.service.denylistActionService
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

        if (denylistActionService.hasLocalAction(name)) {
            executor.sendText {
                appendPrefix()
                error("Die Aktion ")
                variableValue(name)
                error(" ist bereits in der internen Aktionsliste vorhanden.")
            }
            return@anyExecutor
        } else {
            denylistActionService.addLocalAction(
                DenylistActionImpl(
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
            success(" wurde erfolgreich zur internen Aktionsliste hinzugefügt.")
        }

        plugin.launch {
            if (denylistActionService.hasAction(name)) {
                executor.sendText {
                    appendPrefix()
                    error("Die Aktion ")
                    variableValue(name)
                    error(" ist bereits in der externen Aktionsliste vorhanden.")
                }
                return@launch
            }

            denylistActionService.addAction(
                DenylistActionImpl(
                    name,
                    type,
                    reason,
                    durationInMinutes * 60 * 1000L
                )
            )

            executor.sendText {
                appendPrefix()
                success("Die Aktion ")
                variableValue(name)
                success(" wurde erfolgreich zur externen Aktionsliste hinzugefügt.")
            }
        }
    }
}