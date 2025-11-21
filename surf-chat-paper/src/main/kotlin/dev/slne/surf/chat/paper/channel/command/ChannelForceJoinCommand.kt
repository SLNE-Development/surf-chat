package dev.slne.surf.chat.paper.channel.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.paper.channel.argument.channelArgument
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.util.user
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelForceJoinCommand() = subcommand("join") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_ADMIN_JOIN)
    channelArgument("channel")
    playerExecutor { player, args ->
        val channel: Channel by args
        val user = player.user() ?: return@playerExecutor

        if (channelService.getChannel(user) != null) {
            player.sendText {
                appendPrefix()
                error("Du bist bereits in einem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        channel.join(user)

        player.sendText {
            appendPrefix()
            success("Du bist dem Nachrichtenkanal ")
            variableValue(channel.channelName)
            success(" beigetreten.")
        }
    }
}
