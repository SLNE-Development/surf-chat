package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.GreedyStringArgument

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectArraySet

import it.unimi.dsi.fastutil.objects.ObjectSet

class MultiChannelArgument(nodeName: String) : CustomArgument<ObjectSet<ChannelModel>, String>(GreedyStringArgument(nodeName), { info ->
    val input = info.input.trim()

    if (input.isEmpty()) {
        throw CustomArgumentException.fromAdventureComponent {
            buildText {
                appendPrefix()
                error("Du musst mindestens einen Kanal angeben.")
            }
        }
    }

    val returnedChannels: ObjectSet<ChannelModel> = ObjectArraySet()

    if (input.equals("all", ignoreCase = true)) {
        returnedChannels.addAll(channelService.getAllChannels())
    } else {
        val channelNames = input.split("\\s+".toRegex())
        val channels = channelNames.mapNotNull { name -> channelService.getChannel(name) }.toObjectSet()

        if (channels.size != channelNames.size) {
            throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Ein oder mehrere Kanäle existieren.")
                }
            }
        }
        returnedChannels.addAll(channels)
    }

    returnedChannels
}) {
    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
                listOf("all") + channelService.getAllChannels().map { it.name }
            }
        )
    }
}


inline fun CommandAPICommand.multiChannelArgument(nodeName: String, optional: Boolean = false, block: Argument<*>.() -> Unit = {}): CommandAPICommand = withArguments(MultiChannelArgument(nodeName).setOptional(optional).apply(block))
