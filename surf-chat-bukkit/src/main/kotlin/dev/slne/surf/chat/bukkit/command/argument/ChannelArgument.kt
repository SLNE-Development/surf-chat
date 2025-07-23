package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.chat.api.model.Channel

import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelArgument(nodeName: String) :
    CustomArgument<Channel, String>(StringArgument(nodeName), { info ->
        channelService.getChannel(info.input)
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Der Kanal existiert nicht oder ist nicht für dich zugänglich.")
                }
            }
    }) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            channelService.getChannels().map { it.channelName }
        })
    }
}