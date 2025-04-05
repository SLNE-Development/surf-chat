package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.bukkit.command.argument.ChannelMembersArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

import org.bukkit.entity.Player

class ChannelKickCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelMembersArgument("player"))
        playerExecutor { player, args ->
            val channel: ChannelModel? = channelService.getChannel(player)
            val target = args.getUnchecked<Player>("player") ?: return@playerExecutor

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                if (channel == null) {
                    user.sendText(buildText {
                        error("Du bist in keinem Nachrichtenkanal.")
                    })
                    return@launch
                }

                if(!channel.isModerator(user)) {
                    user.sendText(buildText {
                        error("Du bist kein Moderator in diesem Nachrichtenkanal.")
                    })
                    return@launch
                }

                channel.kick(targetUser)
                user.sendText(buildText {
                    primary("Du hast ")
                    info(targetUser.name)
                    primary(" aus dem Nachrichtenkanal ")
                    info(channel.name)
                    error(" geworfen.")
                })

                targetUser.sendText(buildText {
                    primary("Du wurdest aus dem Nachrichtenkanal ")
                    info(channel.name)
                    error(" geworfen.")
                })
            }
        }
    }
}
