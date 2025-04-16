package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.entity.Player

class ChannelUnBanCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        playerArgument("player")
        playerExecutor { player, args ->
            val target = args.getUnchecked<Player>("player") ?: return@playerExecutor
            val channel: ChannelModel? = channelService.getChannel(player)

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
                        error("Du bist nicht der Moderator oder Besitzer des Nachrichtenkanals.")
                    })
                    return@launch
                }

                if (!channel.isBanned(targetUser)) {
                    user.sendText(buildText {
                        error("Der Spieler ")
                        info(targetUser.getName())
                        error(" ist nicht im Nachrichtenkanal gebannt.")
                    })
                    return@launch
                }

                channel.unban(targetUser)

                user.sendText(buildText {
                    primary("Du hast ")
                    info(target.name)
                    primary(" im Nachrichtenkanal ")
                    info(channel.name)
                    error(" entbannt.")
                })

                targetUser.sendText(buildText {
                    primary("Du wurdest im Nachrichtenkanal ")
                    info(channel.name)
                    error(" entbannt.")
                })
            }
        }
    }
}
