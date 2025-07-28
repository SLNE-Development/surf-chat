package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.api.model.ChannelVisibility
import dev.slne.surf.chat.bukkit.command.argument.channelVisibilityArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService

fun CommandAPICommand.channelVisibilityCommand() = subcommand("visibility") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_VISIBILITY)
    channelVisibilityArgument("visibility")
    playerExecutor { player, args ->
        val user = player.user() ?: return@playerExecutor
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
