package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.bukkit.command.argument.ChannelInviteArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelAcceptInviteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withArguments(ChannelInviteArgument("channel"))

        playerExecutor { player, args ->
            val channel = args.getUnchecked<ChannelModel>("channel") ?: return@playerExecutor

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)


                if (!channel.isInvited(user)) {
                    user.sendText(buildText {
                        error("Du hast keine Einladung für den Nachrichtenkanal ")
                        variableValue(channel.name)
                        error(" erhalten.")
                    })
                    return@launch
                }

                user.acceptInvite(channel)
                user.sendText(
                    buildText {
                        primary("Du hast die Einladung für den Nachrichtenkanal ")
                        variableValue(channel.name)
                        success(" angenommen.")
                    }
                )
            }
        }
    }
}
