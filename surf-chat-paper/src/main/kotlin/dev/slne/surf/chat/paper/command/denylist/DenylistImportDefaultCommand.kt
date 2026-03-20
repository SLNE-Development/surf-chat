package dev.slne.surf.chat.paper.command.denylist

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.paper.denylist.DefaultDenyList
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.surfapi.bukkit.api.command.executors.anyExecutorSuspend
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

fun CommandAPICommand.denylistImportDefaultCommand() = subcommand("importdefaults") {
    withPermission(PermissionRegistry.COMMAND_DENYLIST_DEFAULTS)
    anyExecutorSuspend { executor, _ ->
        executor.sendText {
            appendInfoPrefix()
            info("Importiere Standard-Wortfilter...")
        }

        coroutineScope {
            for (batch in DefaultDenyList.default) {
                launch {
                    batch.execute()
                }
            }
        }

        executor.sendText {
            appendSuccessPrefix()
            success("Import der Standard-Wortfilter abgeschlossen.")
        }
    }
}