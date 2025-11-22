package dev.slne.surf.chat.paper.command.denylist

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.api.denylist.DenylistEntry
import dev.slne.surf.chat.core.client.denylist.denylistService
import dev.slne.surf.chat.paper.command.argument.denylistActionArgument
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.util.realName
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.denylistAddCommand() = subcommand("add") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DENYLIST_ADD)
    stringArgument("word")
    denylistActionArgument("action")
    greedyStringArgument("reason", optional = true)
    anyExecutor { executor, args ->
        val word: String by args
        val reason: String? by args
        val action: DenylistAction by args
        val addedAt = System.currentTimeMillis()
        val name = executor.realName()


        if (denylistService.hasEntry(word)) {
            executor.sendText {
                appendPrefix()
                error("Der Eintrag ")
                variableValue(word)
                error(" ist bereits in der Denylist vorhanden.")
            }
            return@anyExecutor
        }

        denylistService.addEntry(
            DenylistEntry(
                word, reason ?: "Verbotenes Wort", name, addedAt, action
            )
        )

        executor.sendText {
            appendPrefix()
            success("Der Eintrag ")
            variableValue(word)
            success(" wurde erfolgreich zur Denylist hinzugefügt.")
        }
    }
}