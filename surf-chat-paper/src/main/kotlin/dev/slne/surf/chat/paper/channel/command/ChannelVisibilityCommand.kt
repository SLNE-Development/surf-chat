package dev.slne.surf.chat.paper.channel.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelVisibility
import dev.slne.surf.chat.paper.channel.argument.channelVisibilityArgument
import dev.slne.surf.chat.paper.channel.channelService
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.util.cloudPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelVisibilityCommand() = subcommand("visibility") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_VISIBILITY)
    channelVisibilityArgument("visibility")
    playerExecutor { player, args ->
        val user = player.cloudPlayer
        val channel: Channel = channelService.getChannel(user) ?: run {
            user.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        if (!channel.isOwner(user)) {
            user.sendText {
                appendPrefix()
                error("Du verfügst nicht über die erforderliche Berechtigung.")
            }
            return@playerExecutor
        }

        val visibility: ChannelVisibility by args

        if (channel.visibility == visibility) {
            user.sendText {
                appendPrefix()
                error("Der Nachrichtenkanal ist bereits auf '${visibility.displayName}' gesetzt.")
            }
            return@playerExecutor
        }

        channel.visibility = visibility

        user.sendText {
            appendPrefix()
            success("Der Nachrichtenkanal ")
            variableValue(channel.channelName)
            success(" ist nun auf '${visibility.displayName}' gesetzt.")
        }
    }
}
