package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.core.service.channelService

class ChannelInviteArgument(nodeName: String) :
    CustomArgument<ChannelModel, String>(StringArgument(nodeName), { info ->
            val channel = channelService.getChannel(info.input())
                ?: throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Der Kanal wurde nicht gefunden.").appendArgInput())

            if (!channel.isInvited(info.sender())) {
                throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Dieser Kanal ist nicht für dich zugänglich.").appendArgInput())
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