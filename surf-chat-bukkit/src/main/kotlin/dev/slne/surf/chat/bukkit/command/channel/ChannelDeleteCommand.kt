package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelDeleteCommand() = subcommand("delete") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_DELETE)
    playerExecutor { player, _ ->
        val user = player.user() ?: return@playerExecutor
        val channel: Channel? = channelService.getChannel(user)

        plugin.launch {
            if (channel == null) {
                player.sendText {
                    error("Du bist in keinem Nachrichtenkanal.")
                }
                return@launch
            }

            if (!channel.isOwner(user)) {
                player.sendText {
                    error("Du bist nicht berechtigt diesen Nachrichtenkanal zu löschen.")
                }
                return@launch
            }

            channelService.deleteChannel(channel)
            player.sendText {
                success("Du hast den Nachrichtenkanal ")
                variableValue(channel.channelName)
                success(" gelöscht.")
            }
        }
    }
}
