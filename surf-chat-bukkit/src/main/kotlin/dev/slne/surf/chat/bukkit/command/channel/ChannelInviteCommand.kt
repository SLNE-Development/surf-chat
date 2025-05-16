package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.EntitySelectorArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.components
import dev.slne.surf.chat.bukkit.util.utils.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.OfflinePlayer

class ChannelInviteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_INVITE)
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

                if (channel.isInvited(targetUser)) {
                    user.sendText(buildText {
                        error("Der Spieler ")
                        variableValue(target.name ?: target.uniqueId.toString())
                        error(" wurde bereits eingeladen.")
                    })
                    return@launch
                }

                if (channel.isMember(targetUser)) {
                    user.sendText(buildText {
                        error("Der Spieler ")
                        variableValue(target.name ?: target.uniqueId.toString())
                        error(" ist bereits in diesem Nachrichtenkanal.")
                    })
                    return@launch
                }

                if (!channel.hasModeratorPermissions(user)) {
                    user.sendText(buildText {
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    })
                    return@launch
                }

                channel.invite(targetUser)

                user.sendText(buildText {
                    info("Du hast ")
                    variableValue(target.name ?: target.uniqueId.toString())
                    info(" in den Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" eingeladen.")
                })

                if (targetUser.channelInvites) {
                    targetUser.sendText(buildText {
                        info("Du wurdest in den Nachrichtenkanal ")
                        variableValue(channel.name)
                        info(" eingeladen. ")

                        append(components.getInviteAcceptComponent(channel))
                        append(components.getInviteDeclineComponent(channel))
                    })
                }
            }
        }
    }
}
