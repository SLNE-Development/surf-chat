package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.chat.api.channel.ChannelVisibility
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelVisibilityArgument(nodeName: String) :
    CustomArgument<ChannelVisibility, String>(StringArgument(nodeName), { info ->
        when (info.input()) {
            "public", "öffentlich", "offen" -> ChannelVisibility.PUBLIC
            "private", "privat", "geschlossen" -> ChannelVisibility.PRIVATE
            else -> throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Bitte gebe entweder 'public', 'private' oder 'privat' an.")
                }
            }
        }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.strings(
                "public", "offen", "private", "privat", "geschlossen"
            )
        )
    }
}

inline fun CommandAPICommand.channelVisibilityArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(ChannelVisibilityArgument(nodeName).setOptional(optional).apply(block))