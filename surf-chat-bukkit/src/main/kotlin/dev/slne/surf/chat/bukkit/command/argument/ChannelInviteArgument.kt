package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelInviteArgument(nodeName: String) :
    CustomArgument<ChannelModel, String>(StringArgument(nodeName), { info ->
            val channel = channelService.getChannel(info.input())
                ?: throw CustomArgumentException.fromAdventureComponent { buildText {
                    appendPrefix()
                    error("Der Kanal exestiert nicht.")
                } }

            if (!channel.isInvited(info.sender())) {
                throw CustomArgumentException.fromAdventureComponent { buildText {
                    appendPrefix()
                    error("Dieser Kanal ist nicht für dich zugänglich.")
                } }
            }
            channel
        }) {

    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection { info ->
            channelService.getAllChannels()
                .filter { it.isInvited(info.sender()) }
                .map { it.name }
            }
        )
    }
}