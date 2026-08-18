package dev.slne.surf.chat.minestom.command.argument

import dev.slne.minestom.lobby.api.command.commandapi.CommandAPI
import dev.slne.minestom.lobby.api.command.commandapi.CommandAPICommand
import dev.slne.minestom.lobby.api.command.commandapi.CommandTree
import dev.slne.minestom.lobby.api.command.commandapi.argument.Argument
import dev.slne.minestom.lobby.api.command.commandapi.argument.CustomArgument
import dev.slne.minestom.lobby.api.command.commandapi.argument.StringArgument
import dev.slne.minestom.lobby.api.command.commandapi.suggestion.ArgumentSuggestions
import dev.slne.surf.api.core.messages.adventure.buildText

class NiceToggleArgument(nodeName: String) :
    CustomArgument<Boolean, String>(StringArgument(nodeName), { info ->
        when (info.currentInput) {
            "enable", "on", "an" -> true
            "disable", "off", "aus" -> false
            else -> CommandAPI.failWithMessage(
                buildText {
                    appendErrorPrefix()
                    error("Bitte gebe entweder 'enable', 'disable', 'on' oder 'off' an.")
                }
            )
        }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.strings(
                "enable",
                "disable",
                "on",
                "off",
                "an",
                "aus"
            )
        )
    }
}

inline fun CommandAPICommand.niceToggleArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<Boolean>.() -> Unit = {}
): CommandAPICommand =
    withArguments(NiceToggleArgument(nodeName).setOptional(optional = optional).apply(block))

inline fun CommandTree.niceToggleArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<Boolean>.() -> Unit = {}
): CommandTree =
    then(NiceToggleArgument(nodeName).setOptional(optional = optional).apply(block))
