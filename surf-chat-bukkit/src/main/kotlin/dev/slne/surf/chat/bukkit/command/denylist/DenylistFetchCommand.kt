package dev.slne.surf.chat.bukkit.command.denylist

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.util.coloredComponent
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.surfapi.bukkit.api.command.executors.anyExecutorSuspend
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlin.system.measureTimeMillis

fun CommandAPICommand.denylistFetchCommand() = subcommand("fetch") {
    withPermission(PermissionRegistry.COMMAND_DENYLIST_FETCH)
    anyExecutorSuspend { executor, args ->
        executor.sendText {
            appendInfoPrefix()
            info("Die Denylist wird aktualisiert...")
        }

        val ms = measureTimeMillis {
            denylistService.fetch()
        }

        executor.sendText {
            appendSuccessPrefix()
            success("Die Denylist wurde erfolgreich aktualisiert")
            appendSpace()
            spacer("(")
            append(ms.coloredComponent(250))
            spacer(")")
            success("!")
        }
    }
}