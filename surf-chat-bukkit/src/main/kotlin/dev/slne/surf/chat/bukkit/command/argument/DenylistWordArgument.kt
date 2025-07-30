package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.chat.core.service.denylistService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class DenylistWordArgument(nodeName: String) :
    CustomArgument<String, String>(StringArgument(nodeName), { info ->
        denylistService.getLocalEntry(info.input)
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Das Wort ist nicht auf der internen Denylist.")
                }
            }

        info.input
    }) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            denylistService.getLocalEntries().map { it.word }
        })
    }
}

inline fun CommandAPICommand.denylistWordArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(DenylistWordArgument(nodeName).setOptional(optional).apply(block))