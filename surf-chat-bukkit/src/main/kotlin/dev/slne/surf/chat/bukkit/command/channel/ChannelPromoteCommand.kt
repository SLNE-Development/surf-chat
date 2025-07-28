package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.bukkit.command.argument.channelMembersArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun CommandAPICommand.channelPromoteCommand() = subcommand("promote") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_PROMOTE)
    channelMembersArgument("target")
    playerExecutor { player, args ->
        val user = player.user() ?: return@playerExecutor
        val channel: Channel = channelService.getChannel(user) ?: run {
            player.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }
        val target: Player by args

        if (!channel.isOwner(user)) {
            user.sendText {
                appendPrefix()
                error("Du verfügst nicht über die erforderliche Berechtigung.")
            }
            return@playerExecutor
        }

        val targetMember = target.user()?.channelMember(channel) ?: run {
            user.sendText {
                appendPrefix()
                error("Der Spieler ")
                variableValue(target.name)
                error(" ist nicht im Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        if (targetMember.hasModeratorPermissions()) {
            user.sendText {
                appendPrefix()
                error("Der Spieler ")
                variableValue(target.name)
                error(" ist bereits Moderator.")
            }
            return@playerExecutor
        }

        channel.promote(targetMember)

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
