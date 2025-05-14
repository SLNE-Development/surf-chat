package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.OfflinePlayer

class ChannelInviteRevokeCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(EntitySelectorArgument.OneEntity("player"))
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

                if (!channel.isModerator(user)) {
                    user.sendText(buildText {
                        error("Du hast keine Moderationsrechte in diesem Nachrichtenkanal.")
                    })
                    return@launch
                }

                if (!channel.isInvited(targetUser)) {
                    user.sendText(buildText {
                        info("Der Spieler ")
                        variableValue(targetUser.getName())
                        info(" hat keine Einladung für diesen Nachrichtenkanal.")
                    })
                    return@launch
                }

                channel.revokeInvite(targetUser)

                user.sendText(buildText {
                    info("Du hast die Einladung des Spielers ")
                    variableValue(targetUser.getName())
                    info(" im Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" zurückgezogen.")
                })

                if (!targetUser.isIgnoringChannelInvites()) {
                    targetUser.sendText(buildText {
                        info("Deine Einladung in den Nachrichtenkanal ")
                        variableValue(channel.name)
                        info(" wurde zurückgezogen.")
                    })
                }
            }
        }
    }
}
