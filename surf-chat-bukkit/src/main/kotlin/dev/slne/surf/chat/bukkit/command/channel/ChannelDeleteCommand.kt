package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.model.ChannelModel
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText

class ChannelDeleteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_DECLINE)
        playerExecutor { player, _ ->
            val channel: ChannelModel? = channelService.getChannel(player)

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)

                if (channel == null) {
                    user.sendText(buildText {
                        error("Du bist in keinem Nachrichtenkanal.")
                    })
                    return@launch
                }

                if (!channel.isOwner(user)) {
                    user.sendText(buildText {
                        error("Du bist nicht berechtigt diesen Nachrichtenkanal zu löschen.")
                    })
                    return@launch
                }

                channelService.deleteChannel(channel)
                user.sendText(buildText {
                    success("Du hast den Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" gelöscht.")
                })
            }
        }

    }
}
