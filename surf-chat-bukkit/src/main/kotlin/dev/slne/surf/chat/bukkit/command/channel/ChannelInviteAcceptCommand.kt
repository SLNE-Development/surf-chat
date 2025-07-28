package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand

import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.bukkit.command.argument.channelInviteArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelInviteAcceptCommand() = subcommand("accept") {
    channelInviteArgument("channel")
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_ACCEPT)
    playerExecutor { player, args ->
        val channel: Channel by args
        val user = player.user() ?: return@playerExecutor

        if (!channel.isInvited(user)) {
            player.sendText {
                appendPrefix()
                error("Du hast keine Einladung für den Nachrichtenkanal ")
                variableValue(channel.channelName)
                error(" erhalten.")
            }
            return@playerExecutor
        }

        if (channelService.acceptInvite(channel, user)) {
            channel.sendText {
                appendPrefix()
                variableValue(user.name)
                info(" hat den Nachrichtenkanal betreten.")
            }
        }
        player.sendText {
            appendPrefix()
            info("Du hast die Einladung für den Nachrichtenkanal ")
            variableValue(channel.channelName)
            success(" angenommen.")
        }
    }
}