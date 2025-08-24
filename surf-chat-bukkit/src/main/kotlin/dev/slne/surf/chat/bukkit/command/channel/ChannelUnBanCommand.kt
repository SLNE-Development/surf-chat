package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.bukkit.command.argument.userArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService

fun CommandAPICommand.channelUnBanCommand() = subcommand("unban") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_UNBAN)
    userArgument("target")
    playerExecutor { player, args ->
        val user = player.user() ?: return@playerExecutor
        val target: User by args
        val channel: Channel = channelService.getChannel(user) ?: run {
            user.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        val userMember = user.channelMember(channel) ?: run {
            user.sendText {
                appendPrefix()
                error("Du bist kein Mitglied in diesem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        if (!userMember.hasModeratorPermissions()) {
            user.sendText {
                appendPrefix()
                error("Du verfügst nicht über die erforderliche Berechtigung.")
            }
            return@playerExecutor
        }

        if (!channel.isBanned(target)) {
            user.sendText {
                appendPrefix()
                error("Der Spieler ")
                variableValue(target.name)
                error(" ist nicht im Nachrichtenkanal gebannt.")
            }
            return@playerExecutor
        }

        channel.unban(target)

        user.sendText {
            appendPrefix()
            success("Du hast den Spieler ")
            variableValue(target.name)
            success(" im Nachrichtenkanal ")
            variableValue(channel.channelName)
            success(" entbannt.")
        }

        target.sendText {
            appendPrefix()
            info("Du wurdest im Nachrichtenkanal ")
            variableValue(channel.channelName)
            info(" entbannt.")
        }
    }
}
