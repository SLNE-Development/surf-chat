package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class NiceToggleArgument(nodeName: String) :
    CustomArgument<Boolean, String>(StringArgument(nodeName), { info ->
        when (info.input()) {
            "enable", "on", "an" -> true
            "disable", "off", "aus" -> false
            else -> throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Bitte gebe entweder 'enable', 'disable', 'on' oder 'off' an.")
                }
            }
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
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(NiceToggleArgument(nodeName).setOptional(optional).apply(block))