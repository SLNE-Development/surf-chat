package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.OfflinePlayerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.components
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.OfflinePlayer

class ChannelInviteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(OfflinePlayerArgument("player"))
        playerExecutor { player, args ->
            val channel: ChannelModel? = channelService.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@playerExecutor

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

                if (channel == null) {
                    user.sendText(buildText {
                        error("Du bist in keinem Nachrichtenkanal.")
                    })
                    return@launch
                }

                if(channel.isInvited(targetUser)) {
                    user.sendText(buildText {
                        error("Der Spieler ")
                        info(target.name ?: target.uniqueId.toString())
                        error(" wurde bereits eingeladen.")
                    })
                    return@launch
                }

                if(channel.isMember(targetUser)) {
                    user.sendText(buildText {
                        error("Der Spieler ")
                        info(target.name ?: target.uniqueId.toString())
                        error(" ist bereits in diesem Nachrichtenkanal.")
                    })
                    return@launch
                }

                if(!channel.isModerator(user)) {
                    user.sendText(buildText {
                        error("Du bist kein Moderator in diesem Nachrichtenkanal.")
                    })
                    return@launch
                }

                channel.invite(targetUser)

                user.sendText(buildText {
                    primary("Du hast ")
                    info(target.name ?: target.uniqueId.toString())
                    primary(" in den Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" eingeladen.")
                })

                targetUser.sendText(buildText {
                    primary("Du wurdest in den Nachrichtenkanal ")
                    info(channel.name)
                    success(" eingeladen.")

                    append(components.getInviteAcceptComponent(channel))
                    append(components.getInviteDeclineComponent(channel))
                })
            }
        }
    }
}
