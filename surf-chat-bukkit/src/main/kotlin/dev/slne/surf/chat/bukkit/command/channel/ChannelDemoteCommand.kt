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

class ChannelDemoteCommand(commandName: String) : CommandAPICommand(commandName) {
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

                if(target.uniqueId == player.uniqueId) {
                    user.sendText (buildText {
                        error("Du kannst dich nicht selbst degradieren.")
                    })
                }

                if (!channel.isOwner(user)) {
                    user.sendText(
                        buildText {
                            error("Du verfügst nicht über die erforderliche Berechtigung.")
                        }
                    )
                    return@launch
                }

                if (!channel.isMember(targetUser)) {
                    user.sendText(buildText {
                        error("Der Spieler ")
                        variableValue(target.name)
                        error(" ist kein Mitglied in dem Nachrichtenkanal.")
                    })
                    return@launch
                }

                if(!channel.isModerator(targetUser) && !channel.isOwner(targetUser)) {
                    user.sendText(buildText {
                        error("Der Spieler ")
                        variableValue(target.name)
                        error(" ist bereits ein Mitglied.")
                    })
                    return@launch
                }

                channel.demote(targetUser)

                user.sendText(buildText {
                    success("Du hast ")
                    variableValue(target.name)
                    success(" degradiert.")
                })

                targetUser.sendText(buildText {
                    info("Du wurdest degradiert.")
                })
            }
        }
    }
}
