package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.core.service.userService
import dev.slne.surf.core.api.common.surfCoreApi
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class UserArgument(nodeName: String) :
    CustomArgument<User, String>(StringArgument(nodeName), { info ->
        userService.findUserByName(info.input)
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Der Spieler wurde nicht gefunden.")
                }
            }
    }) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            surfCoreApi.getOnlinePlayers().mapNotNull { it.lastKnownName }
        })
    }
}

inline fun CommandAPICommand.userArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(UserArgument(nodeName).setOptional(optional).apply(block))