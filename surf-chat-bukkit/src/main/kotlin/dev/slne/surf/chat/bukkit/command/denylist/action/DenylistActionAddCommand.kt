package dev.slne.surf.chat.bukkit.command.denylist.action

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.chat.api.entry.DenylistActionType
import dev.slne.surf.chat.bukkit.command.argument.denylistActionArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.surfapi.bukkit.api.command.args.miniMessageArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import net.kyori.adventure.text.Component

fun CommandAPICommand.denylistActionAddCommand() = subcommand("add") {
    withPermission(SurfChatPermissionRegistry.COMMAND_DENYLIST_ACTION_ADD)
    stringArgument("name")
    denylistActionArgument("type")
    miniMessageArgument("reason")
    integerArgument("durationInMinutes", 0)
    anyExecutor { executor, args ->
        val name: String by args
        val type: DenylistActionType by args
        val reason: Component by args
        val durationInMinutes: Int by args


        if (denylistService.hasLocalEntry(word)) {
            executor.sendText {
                appendPrefix()
                error("Der Eintrag ")
                variableValue(word)
                error(" ist bereits in der internen Denylist vorhanden.")
            }
            return@anyExecutor
        }

        denylistService.addLocalEntry(word, reason ?: "Verbotenes Wort", name, addedAt)

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

            denylistService.addEntry(word, reason ?: "Verbotenes Wort", name, addedAt)

            executor.sendText {
                appendPrefix()
                success("Der Eintrag ")
                variableValue(word)
                success(" wurde erfolgreich zur externen Denylist hinzugefügt.")
            }
        }
    }
}