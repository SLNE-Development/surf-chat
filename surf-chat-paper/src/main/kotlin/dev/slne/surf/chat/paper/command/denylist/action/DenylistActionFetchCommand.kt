package dev.slne.surf.chat.paper.command.denylist.action

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.core.service.denylistActionService
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.coloredComponent
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlin.system.measureTimeMillis

fun CommandAPICommand.denylistActionFetchCommand() = subcommand("fetch") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DENYLIST_ACTION_FETCH)
    anyExecutor { executor, _ ->
        executor.sendText {
            appendPrefix()
            info("Die Denylist Aktionen werden aktualisiert...")
        }

        plugin.launch {
            val ms = measureTimeMillis {
                denylistActionService.fetchActions()
            }

            executor.sendText {
                appendPrefix()
                success("Die Denylist Aktionen wurde erfolgreich aktualisiert")
                appendSpace()
                spacer("(")
                append(ms.coloredComponent(250))
                spacer(")")
                success("!")
            }
        }
    }
}