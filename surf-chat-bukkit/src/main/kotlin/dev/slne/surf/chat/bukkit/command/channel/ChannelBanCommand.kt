package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.social.chat.command.argument.ChannelMembersArgument
import org.bukkit.OfflinePlayer

class ChannelBanCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelMembersArgument("player"))
        playerExecutor { player, args ->
            val channel: ChannelModel? = channelService.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@playerExecutor

            if (channel == null) {
                SurfChat.send(player, MessageBuilder().error("Du bist in keinem Nachrichtenkanal."))
                return@playerExecutor
            }

            if (!channel.isModerator(player) && !channel.isOwner(player)) {
                SurfChat.send(player, MessageBuilder().primary("Du bist ").error("kein Moderator ").primary("in deinem Kanal."))
                return@playerExecutor
            }

            channel.ban(target)

            SurfChat.send(player, MessageBuilder().primary("Du hast ").info(target.name ?: target.uniqueId.toString()).primary(" aus dem Nachrichtenkanal ").info(channel.name).error(" verbannt."))
            SurfChat.send(target, MessageBuilder().primary("Du wurdest aus dem Nachrichtenkanal ").info(channel.name).error(" verbannt."))
        }

    }
}
