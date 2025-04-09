package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.util.emptyObjectSet
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class ChannelMembersArgument(nodeName: String) : CustomArgument<Player, String>(StringArgument(nodeName), { info ->
            val player = Bukkit.getPlayer(info.input()) ?: throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Der Spieler ${info.input} wurde nicht gefunden."))
            val channel = channelService.getChannel(player) ?: throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Du bist in keinem Kanal, oder dieser ist invalid."))

            if (!channel.isMember(player)) {
                throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Der Spieler ${player.name} ist kein Mitglied in deinem Nachrichtenkanal."))
            }

            player
        }) {
    init {
        this.replaceSuggestions(ArgumentSuggestions.stringCollection { info ->
            val channel = channelService.getChannel(info.sender) ?: return@stringCollection emptyObjectSet()
            val members = channel.members

            if (members.isEmpty()) {
                return@stringCollection emptyObjectSet()
            }

            return@stringCollection members.keys.map { it.getName() }
        })
    }
}