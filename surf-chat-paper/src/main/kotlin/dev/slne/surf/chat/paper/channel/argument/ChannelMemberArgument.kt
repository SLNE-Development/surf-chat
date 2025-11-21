package dev.slne.surf.chat.paper.channel.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.paper.channel.channelService
import dev.slne.surf.chat.paper.util.channelMember
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.cloud.api.common.player.toCloudPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.emptyObjectSet

class ChannelMemberArgument(nodeName: String) :
    CustomArgument<ChannelMember, String>(StringArgument(nodeName), { info ->
        val user =
            CloudPlayer[info.input] ?: throw CustomArgumentException.fromMessageBuilder(
                MessageBuilder("Der Spieler ${info.input} wurde nicht gefunden.")
            )

        val channel = channelService.getChannel(user)
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Du bist in keinem Nachrichtenkanal.")
                }
            }

        if (!channel.isMember(user)) {
            throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Der Spieler ${user.name} ist kein Mitglied in deinem Nachrichtenkanal.")
                }
            }
        }

        user.channelMember(channel)
    }) {
    init {
        this.replaceSuggestions(ArgumentSuggestions.stringCollection { info ->
            val user = info.sender.toCloudPlayer() ?: return@stringCollection emptyObjectSet()
            val channel =
                channelService.getChannel(user) ?: return@stringCollection emptyObjectSet()
            val members = channel.members

            if (members.isEmpty()) {
                return@stringCollection emptyObjectSet()
            }

            return@stringCollection members.map {
                it.name
            }
        })
    }
}

inline fun CommandAPICommand.channelMemberArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(ChannelMemberArgument(nodeName).setOptional(optional).apply(block))