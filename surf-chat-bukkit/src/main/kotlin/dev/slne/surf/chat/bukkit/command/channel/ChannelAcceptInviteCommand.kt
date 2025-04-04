package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.chat.bukkit.command.argument.ChannelInviteArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder

class ChannelAcceptInviteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelInviteArgument("channel"))

        playerExecutor {player, args ->
            val channel = args.getUnchecked<ChannelModel>("channel") ?: return@playerExecutor

            if (!channel.isInvited(player)) {
                SurfChat.send(player, MessageBuilder().primary("Du hast keine Einladung in den Nachrichtenkanal ").info(channel.name).error(" erhalten."))
                return@playerExecutor
            }

            channel.acceptInvite(player)
            SurfChat.send(player, MessageBuilder().primary("Du hast die Einladung in den Nachrichtenkanal ").info(channel.name).success(" angenommen."))
        }
    }
}
