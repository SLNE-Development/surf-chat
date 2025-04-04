package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.command.argument.ChannelMembersArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class ChannelPromoteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelMembersArgument("player"))
        playerExecutor { player, args ->
            val channel: Channel? = Channel.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@playerExecutor

            if(channel == null) {
                SurfChat.send(player, MessageBuilder().error("Du bist in keinem Nachrichtenkanal."))
                return@playerExecutor
            }

            if (!channel.isOwner(player)) {
                SurfChat.send(
                    player,
                    MessageBuilder().error("Du bist nicht der Besitzer des Nachrichtenkanals.")
                )
                return@playerExecutor
            }

            channel.promote(target.uniqueId)

            SurfChat.send(player, MessageBuilder().primary("Du hast den Spieler ").info(target.name ?: target.uniqueId.toString()).success(" befördert."))
            SurfChat.send(target, MessageBuilder().primary("Du wurdest ").success("befördert"))
        }
    }
}
