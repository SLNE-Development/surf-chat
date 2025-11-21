package dev.slne.surf.chat.paper.channel.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelVisibility
import dev.slne.surf.chat.paper.channel.argument.channelArgument
import dev.slne.surf.chat.paper.channel.channelService
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.util.cloudPlayer
import dev.slne.surf.chat.paper.util.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelJoinCommand() = subcommand("join") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_JOIN)
    channelArgument("channel")
    playerExecutor { player, args ->
        val channel: Channel by args
        val user = player.cloudPlayer

        if (channelService.getChannel(user) != null) {
            user.sendText {
                appendPrefix()
                error("Du bist bereits in einem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        if (channel.isBanned(user)) {
            user.sendText {
                appendPrefix()
                error("Du wurdest von diesem Nachrichtenkanal ausgeschlossen.")
            }
            return@playerExecutor
        }

        if (channel.visibility != ChannelVisibility.PUBLIC && !channel.isInvited(user)) {
            user.sendText {
                appendPrefix()
                error("Der Nachrichtenkanal ")
                variableValue(channel.channelName)
                error(" ist privat.")
            }
            return@playerExecutor
        }

        channel.join(user)
        channel.sendText {
            appendPrefix()
            variableValue(user.name)
            info(" hat den Nachrichtenkanal betreten.")
        }
    }
}
