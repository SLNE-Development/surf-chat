package dev.slne.surf.chat.paper.command.denylist

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.core.common.service.denylistService
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.coloredComponent
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlin.system.measureTimeMillis

fun CommandAPICommand.denylistFetchCommand() = subcommand("fetch") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DENYLIST_FETCH)
    anyExecutor { executor, args ->
        executor.sendText {
            appendPrefix()
            info("Die Denylist wird aktualisiert...")
        }

        plugin.launch {
            val ms = measureTimeMillis {
                denylistService.fetch()
            }

            executor.sendText {
                appendPrefix()
                success("Die Denylist wurde erfolgreich aktualisiert")
                appendSpace()
                spacer("(")
                append(ms.coloredComponent(250))
                spacer(")")
                success("!")
            }
        }
    }
}