package dev.slne.surf.social.chat.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.offlinePlayerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.social.chat.SurfChat
import dev.slne.surf.social.chat.`object`.Channel
import dev.slne.surf.social.chat.util.MessageBuilder
import org.bukkit.OfflinePlayer

class ChannelUnBanCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        offlinePlayerArgument("player")
        playerExecutor { player, args ->
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@playerExecutor
            val channel: Channel? = Channel.getChannel(player)

            if (channel == null) {
                SurfChat.send(player, MessageBuilder().error("Du bist in keinem Nachrichtenkanal."))
                return@playerExecutor
            }

            if (!channel.isModerator(player) && !channel.isOwner(player)) {
                SurfChat.send(player, MessageBuilder().error("Du bist nicht der Moderator oder Besitzer des Nachrichtenkanals."))
                return@playerExecutor
            }

            channel.unban(target.uniqueId)

            SurfChat.send(player, MessageBuilder().primary("Du hast ").info(target.name ?: target.uniqueId.toString()).primary(" im Nachrichtenkanal ").info(channel.name).error(" entbannt."))
            SurfChat.send(target, MessageBuilder().primary("Du wurdest im Nachrichtenkanal ").info(channel.name).error(" entbannt."))
        }
    }
}
