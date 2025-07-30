package dev.slne.surf.chat.bukkit.command.denylist

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.realName
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.denylistAddCommand() = subcommand("add") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DENYLIST_ADD)
    stringArgument("word")
    greedyStringArgument("reason")
    anyExecutor { executor, args ->
        val word: String by args
        val reason: String by args
        val addedAt = System.currentTimeMillis()
        val name = executor.realName()


        if (denylistService.hasLocalEntry(word)) {
            executor.sendText {
                appendPrefix()
                error("Der Eintrag ")
                variableValue(word)
                error(" ist bereits in der internen Denylist vorhanden.")
            }
            return@anyExecutor
        }

        denylistService.addLocalEntry(word, reason, name, addedAt)

        executor.sendText {
            appendPrefix()
            success("Der Eintrag ")
            variableValue(word)
            success(" wurde erfolgreich zur internen Denylist hinzugefügt.")
        }

        plugin.launch {
            if (denylistService.hasEntry(word)) {
                executor.sendText {
                    appendPrefix()
                    error("Der Eintrag ")
                    variableValue(word)
                    error(" ist bereits in der externen Denylist vorhanden.")
                }
                return@launch
            }

            denylistService.addEntry(word, reason, name, addedAt)

            executor.sendText {
                appendPrefix()
                success("Der Eintrag ")
                variableValue(word)
                success(" wurde erfolgreich zur externen Denylist hinzugefügt.")
            }
        }
    }
}