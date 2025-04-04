package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.core.service.channelService

class ChannelArgument(nodeName: String): CustomArgument<ChannelModel, String>(StringArgument(nodeName), {
    info -> channelService.getChannel(info.input) ?: throw CustomArgumentException.fromMessageBuilder(
            MessageBuilder("Unknown channel: ").appendArgInput()
        )
    }) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection { channelService.getAllChannels().map { it.name } })
    }
}