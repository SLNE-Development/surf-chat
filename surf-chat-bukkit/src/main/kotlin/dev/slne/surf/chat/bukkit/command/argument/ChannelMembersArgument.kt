package dev.slne.surf.chat.bukkit.command.argument

import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.util.emptyObjectSet
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class ChannelMembersArgument(nodeName: String) : CustomArgument<Player, String>(StringArgument(nodeName), { info ->
    val target = Bukkit.getPlayer(info.input()) ?: throw CustomArgumentException.fromMessageBuilder(MessageBuilder("Der Spieler ${info.input} wurde nicht gefunden."))
    val player = info.sender as? Player ?: throw CustomArgumentException.fromAdventureComponent { buildText {
        appendPrefix()
        error("Der Befehl kann nur von einem Spieler ausgeführt werden.")
    } }

    val channel = channelService.getChannel(player) ?: throw CustomArgumentException.fromAdventureComponent { buildText {
        appendPrefix()
        error("Du bist in keinem Nachrichtenkanal.")
    } }

    if (!channel.isMember(target)) {
        throw CustomArgumentException.fromAdventureComponent { buildText {
            appendPrefix()
            error("Der Spieler ${target.name} ist kein Mitglied in deinem Nachrichtenkanal.")
        } }
    }

    target
}) {
    init {
        this.replaceSuggestions(ArgumentSuggestions.stringCollection { info ->
            val channel = channelService.getChannel(info.sender) ?: return@stringCollection emptyObjectSet()
            val members = channel.members

            if (members.isEmpty()) {
                return@stringCollection emptyObjectSet()
            }

            return@stringCollection members.keys.map { Bukkit.getOfflinePlayer(it.uuid).name ?: "Unknown" }
        })
    }
}