package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.bukkit.command.argument.channelMemberArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelPromoteCommand() = subcommand("promote") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_PROMOTE)
    channelMemberArgument("target")
    playerExecutor { player, args ->
        val user = player.user() ?: return@playerExecutor
        val channel: Channel = channelService.getChannel(user) ?: run {
            player.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }
        val target: ChannelMember by args

        if (!channel.isOwner(user)) {
            user.sendText {
                appendPrefix()
                error("Du verfügst nicht über die erforderliche Berechtigung.")
            }
            return@playerExecutor
        }

        if (target.hasModeratorPermissions()) {
            user.sendText {
                appendPrefix()
                error("Der Spieler ")
                variableValue(target.name)
                error(" ist bereits Moderator.")
            }
            return@playerExecutor
        }

        channel.promote(target)

        user.sendText {
            appendPrefix()
            success("Du hast ")
            variableValue(target.name)
            success(" befördert.")
        }

        target.sendText {
            appendPrefix()
            info("Du wurdest befördert.")
        }
    }
}
