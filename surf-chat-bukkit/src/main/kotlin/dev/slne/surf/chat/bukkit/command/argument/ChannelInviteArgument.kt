package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelInviteArgument(nodeName: String) :
    CustomArgument<Channel, String>(StringArgument(nodeName), { info ->
        val channel = channelService.getChannel(info.input())
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Der Kanal existiert nicht.")
                }
            }

        val user = info.sender.user() ?: throw CustomArgumentException.fromAdventureComponent {
            buildText {
                appendPrefix()
                error("Ein Fehler ist aufgetreten.")
            }
        }

        if (!channel.isInvited(user)) {
            throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Du bist nicht in diesen Kanal eingeladen.")
                }
            }
        }
        channel
    }) {

    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection { info ->
            val user = info.sender().user() ?: return@stringCollection emptyList()

            channelService.getChannels()
                .filter { it.isInvited(user) }
                .map { it.channelName }
        }
        )
    }
}

inline fun CommandAPICommand.channelInviteArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(ChannelInviteArgument(nodeName).setOptional(optional).apply(block))