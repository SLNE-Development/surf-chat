package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelLeaveCommand() = subcommand("leave") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_LEAVE)
    playerExecutor { player, _ ->
        val user = player.user() ?: return@playerExecutor
        val channel: Channel = channelService.getChannel(user) ?: run {
            player.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        val channelMember = user.channelMember(channel) ?: run {
            player.sendText {
                appendPrefix()
                error("Du bist nicht in diesem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        channel.leaveAndTransfer(channelMember)

        channel.sendText {
            appendPrefix()
            variableValue(user.name)
            info(" hat den Nachrichtenkanal verlassen.")
        }

        player.sendText {
            appendPrefix()
            success("Du hast den Nachrichtenkanal ")
            variableValue(channel.channelName)
            success(" verlassen.")
        }
    }
}