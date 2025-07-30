package dev.slne.surf.chat.bukkit.command.denylist

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.anyExecutor
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.command.argument.denylistWordArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.denylistRemoveCommand() = subcommand("remove") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DENYLIST_REMOVE)
    denylistWordArgument("word")
    anyExecutor { executor, args ->
        val word: String by args

        if (!denylistService.hasLocalEntry(word)) {
            executor.sendText {
                appendPrefix()
                error("Der Eintrag ")
                variableValue(word)
                error(" ist nicht in der internen Denylist vorhanden.")
            }
            return@anyExecutor
        }

        denylistService.removeLocalEntry(word)

        executor.sendText {
            appendPrefix()
            success("Der Eintrag ")
            variableValue(word)
            success(" wurde erfolgreich aus der internen Denylist gelöscht.")
        }

        plugin.launch {
            if (!denylistService.hasEntry(word)) {
                executor.sendText {
                    appendPrefix()
                    error("Der Eintrag ")
                    variableValue(word)
                    error(" ist nicht in der externen Denylist vorhanden.")
                }
                return@launch
            }

            denylistService.removeEntry(word)

            executor.sendText {
                appendPrefix()
                success("Der Eintrag ")
                variableValue(word)
                success(" wurde erfolgreich aus der externen Denylist gelöscht.")
            }
        }
    }
}