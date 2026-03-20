package dev.slne.surf.chat.paper.command.denylist

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.chat.api.denylist.DenylistAction
import dev.slne.surf.chat.paper.command.argument.denylistActionArgument
import dev.slne.surf.chat.paper.permission.PermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.uuidOrNull
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import java.time.OffsetDateTime

fun CommandAPICommand.denylistAddCommand() = subcommand("add") {
    withPermission(PermissionRegistry.COMMAND_DENYLIST_ADD)
    stringArgument("word")
    denylistActionArgument("action")
    greedyStringArgument("reason", optional = true)
    anyExecutor { executor, args ->
        val word: String by args
        val reason: String? by args
        val action: DenylistAction by args
        val addedAt = OffsetDateTime.now()
        val uuid = executor.uuidOrNull()


        if (denylistService.hasLocalEntry(word)) {
            executor.sendText {
                appendErrorPrefix()
                error("Der Eintrag ")
                variableValue(word)
                error(" ist bereits in der internen Denylist vorhanden.")
            }
            return@anyExecutor
        }

        denylistService.addLocalEntry(word, reason ?: "Verbotenes Wort", uuid, addedAt, action)

        executor.sendText {
            appendSuccessPrefix()
            success("Der Eintrag ")
            variableValue(word)
            success(" wurde erfolgreich zur internen Denylist hinzugefügt.")
        }

        plugin.launch {
            if (denylistService.hasEntry(word)) {
                executor.sendText {
                    appendErrorPrefix()
                    error("Der Eintrag ")
                    variableValue(word)
                    error(" ist bereits in der externen Denylist vorhanden.")
                }
                return@launch
            }

            denylistService.addEntry(word, reason ?: "Verbotenes Wort", uuid, action)

            executor.sendText {
                appendSuccessPrefix()
                success("Der Eintrag ")
                variableValue(word)
                success(" wurde erfolgreich zur externen Denylist hinzugefügt.")
            }
        }
    }
}