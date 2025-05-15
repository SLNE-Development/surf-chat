package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor

import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.bukkit.command.argument.ChannelMembersArgument
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

import org.bukkit.entity.Player

class ChannelKickCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_KICK)
        withArguments(ChannelMembersArgument("player"))
        playerExecutor { player, args ->
            val channel: ChannelModel? = channelService.getChannel(player)
            val target = args.getUnchecked<Player>("player") ?: return@playerExecutor

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
                        error("Du verfügst nicht über die erforderliche Berechtigung.")
                    })
                    return@launch
                }

                if (channel.isModerator(targetUser) && channel.isModerator(user)) {
                    user.sendText(buildText {
                        error("Du kannst keine Spieler aus diesem Kanal entfernen.")
                    })
                    return@launch
                }

                channel.kick(targetUser)
                user.sendText(buildText {
                    info("Du hast ")
                    variableValue(targetUser.getName())
                    info(" aus dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" geworfen.")
                })

                targetUser.sendText(buildText {
                    info("Du wurdest aus dem Nachrichtenkanal ")
                    variableValue(channel.name)
                    info(" geworfen.")
                })
            }
        }
    }
}
