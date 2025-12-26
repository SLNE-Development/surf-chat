package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import org.bukkit.Bukkit

class UserStringArgument(nodeName: String) :
    CustomArgument<String, String>(StringArgument(nodeName), { info ->
        info.input
    }) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            Bukkit.getOnlinePlayers().map { it.name }
        })
    }
}

inline fun CommandAPICommand.userStringArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(UserStringArgument(nodeName).setOptional(optional).apply(block))