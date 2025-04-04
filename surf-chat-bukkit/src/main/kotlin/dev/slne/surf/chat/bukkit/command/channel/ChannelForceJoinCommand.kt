package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.chat.bukkit.command.argument.ChannelArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder

class ChannelForceJoinCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.channel.forcejoin")
        withArguments(ChannelArgument("channel"))
        playerExecutor { player, args ->
            val channel = args.getUnchecked<Channel>("channel") ?: return@playerExecutor

            if (Channel.getChannel(player) != null) {
                SurfChat.send(player, MessageBuilder().error("Du bist bereits in einem Nachrichtenkanal."))
                return@playerExecutor
            }

            channel.join(player.uniqueId)
            SurfChat.send(player, MessageBuilder().primary("Du bist dem Nachrichtenkanal ").info(channel.name).success(" beigetreten."))
        }
    }
}
