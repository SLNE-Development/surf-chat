package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.userService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.emptyObjectSet

class ChannelMemberArgument(nodeName: String) :
    CustomArgument<ChannelMember, String>(StringArgument(nodeName), { info ->
        val target =
            userService.getUser(info.input) ?: throw CustomArgumentException.fromMessageBuilder(
                MessageBuilder("Der Spieler ${info.input} wurde nicht gefunden.")
            )
        val user = info.sender.user() ?: throw CustomArgumentException.fromAdventureComponent {
            buildText {
                appendPrefix()
                error("Ein Fehler ist aufgetreten.")
            }
        }

        val channel = channelService.getChannel(user)
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Du bist in keinem Nachrichtenkanal.")
                }
            }

        if (!channel.isMember(target)) {
            throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Der Spieler ${target.name} ist kein Mitglied in deinem Nachrichtenkanal.")
                }
            }
        }

        target.channelMember(channel)
    }) {
    init {
        this.replaceSuggestions(ArgumentSuggestions.stringCollection { info ->
            val user = info.sender.user() ?: return@stringCollection emptyObjectSet()
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