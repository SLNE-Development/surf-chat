package dev.slne.surf.chat.velocity.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.ChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.utils.sendPrefixed
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.core.service.databaseService

class ChannelDeleteCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(ChatPermissionRegistry.COMMAND_CHANNEL_DELETE)
        playerExecutor { player, _ ->
            val channel: Channel? = channelService.getChannel(player)

            plugin.launch {
                val user = databaseService.getUser(player.uniqueId)

                if (channel == null) {
                    user.sendPrefixed {
                        error("Du bist in keinem Nachrichtenkanal.")
                    }
                    return@launch
                }

                if (!channel.isOwner(user)) {
                    user.sendPrefixed {
                        error("Du bist nicht berechtigt diesen Nachrichtenkanal zu löschen.")
                    }
                    return@launch
                }

                channelService.deleteChannel(channel)
                user.sendPrefixed {
                    success("Du hast den Nachrichtenkanal ")
                    variableValue(channel.name)
                    success(" gelöscht.")
                }
            }
        }

    }
}
