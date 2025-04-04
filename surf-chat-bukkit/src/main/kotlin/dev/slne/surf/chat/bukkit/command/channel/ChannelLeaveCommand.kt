package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandArguments
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.entity.Player

class ChannelLeaveCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        playerExecutor { player, _ ->
            val channel: Channel? = Channel.getChannel(player)

            if (channel == null) {
                SurfChat.send(player, MessageBuilder().error("Du bist in keinem Nachrichtenkanal."))
                return@playerExecutor
            }

            channel.leave(player.uniqueId)
            SurfChat.send(player, MessageBuilder().primary("Du hast den Nachrichtenkanal ").info(channel.name).error(" verlassen."))
        }
    }
}
