package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.OfflinePlayer

class ChannelInviteRevokeCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_REVOKE)
        withArguments(EntitySelectorArgument.OnePlayer("player"))
        playerExecutor { player, args ->
            val channel: ChannelModel? = channelService.getChannel(player)
            val target = args.getUnchecked<OfflinePlayer>("player") ?: return@playerExecutor

            if(channel == null) {
                surfChatApi.sendText(player, buildText {
                    error("Du bist in keinem Nachrichtenkanal.")
                })
                return@playerExecutor
            }

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)
                val targetUser = databaseService.getUser(target.uniqueId)

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

                if (targetUser.channelInvites) {
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
