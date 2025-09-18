package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.chat.api.DenylistAction
import dev.slne.surf.chat.core.service.denylistActionService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class DenylistActionArgument(nodeName: String) :
    CustomArgument<DenylistAction, String>(StringArgument(nodeName), { info ->
        denylistActionService.getLocalAction(info.input)
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Diese Action wurde nicht gefunden.")
                }
            }
    }) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            denylistActionService.listLocalActions().map { it.name }
        })
    }
}

inline fun CommandAPICommand.denylistActionArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(DenylistActionArgument(nodeName).setOptional(optional).apply(block))