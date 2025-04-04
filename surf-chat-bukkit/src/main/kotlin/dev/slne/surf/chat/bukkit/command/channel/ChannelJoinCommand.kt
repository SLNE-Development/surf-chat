package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.chat.bukkit.command.argument.ChannelArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder

class ChannelJoinCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelArgument("channel"))
        playerExecutor { player, args ->
            val channel = args.getUnchecked<Channel>("channel") ?: return@playerExecutor

            if (channel.closed && !channel.hasInvite(player)) {
                SurfChat.send(player, MessageBuilder().error("Der Nachrichtenkanal ist privat."))
                return@playerExecutor
            }

            if (Channel.getChannel(player) != null) {
                SurfChat.send(player, MessageBuilder().error("Du bist bereits in einem Nachrichtenkanal."))
                return@playerExecutor
            }

            channel.join(player.uniqueId)
            SurfChat.send(player, MessageBuilder().primary("Du bist dem Nachrichtenkanal ").info(channel.name).success(" beigetreten."))
        }
    }
}
