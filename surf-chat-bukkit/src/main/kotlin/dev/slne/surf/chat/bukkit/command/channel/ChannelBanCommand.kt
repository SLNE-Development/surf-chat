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

class ChannelBanCommand(commandName: String) : CommandAPICommand(commandName) {
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

                if (!channel.isModerator(user)) {
                    user.sendText(buildText {
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    })
                    return@launch
                }

                if (channel.isModerator(targetUser) && channel.isModerator(user)) {
                    user.sendText(buildText {
                        error("Du kannst diesen Spieler nicht entfernen.")
                    })
                    return@launch
                }

                channel.ban(targetUser)

                user.sendText(buildText {
                    success("Du hast ")
                    variableValue(target.name)
                    success(" aus dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" verbannt.")
                })
                targetUser.sendText(buildText {
                    info("Du wurdest aus dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" verbannt.")
                })
            }
        }
    }
}
