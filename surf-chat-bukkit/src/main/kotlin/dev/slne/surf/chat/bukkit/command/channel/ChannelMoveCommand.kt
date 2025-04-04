package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.offlinePlayerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.chat.bukkit.command.argument.ChannelArgument
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.OfflinePlayer

class ChannelMoveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        offlinePlayerArgument("player")
        withOptionalArguments(ChannelArgument("channel"))
        withPermission("surf.chat.command.channel.move")

        playerExecutor { player, args ->
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@playerExecutor
            val channel = args.getUnchecked<Channel>("channel") ?: return@playerExecutor

            channel.move(target.uniqueId, channel)

            SurfChat.send(player, MessageBuilder().primary("Du hast ").info(target.name ?: target.uniqueId.toString()).primary(" in den Nachrichtenkanal ").info(channel.name).success(" verschoben."))
            SurfChat.send(target, MessageBuilder().primary("Du wurdest in den Nachrichtenkanal ").info(channel.name).success(" verschoben."))
        }
    }
}
