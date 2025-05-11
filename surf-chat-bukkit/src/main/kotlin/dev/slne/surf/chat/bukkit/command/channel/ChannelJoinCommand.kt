package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.type.ChannelStatusType
import dev.slne.surf.chat.bukkit.command.argument.ChannelArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelJoinCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelArgument("channel"))
        playerExecutor { player, args ->
            val channel = args.getUnchecked<ChannelModel>("channel") ?: return@playerExecutor

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)

                if(channel.status != ChannelStatusType.PUBLIC && !channel.isInvited(user)) {
                    user.sendText (
                        buildText {
                            error("Der Nachrichtenkanal ")
                            variableValue(channel.name)
                            error(" ist privat.")
                        }
                    )
                    return@launch
                }

                if (channelService.getChannel(player) != null) {
                    user.sendText (
                        buildText {
                            error("Du bist bereits in einem Nachrichtenkanal.")
                        }
                    )
                    return@launch
                }

                if(channel.isBanned(user)) {
                    user.sendText (
                        buildText {
                            error("Du wurdest von diesem Nachrichtenkanal ausgeschlossen.")
                        }
                    )
                    return@launch
                }


                channel.join(user)
                user.sendText (
                    buildText {
                        info("Du bist dem Nachrichtenkanal ")
                        variableValue(channel.name)
                        info(" beigetreten.")
                    }
                )
            }
        }
    }
}
