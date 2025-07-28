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

fun CommandAPICommand.channelKickCommand() = subcommand("kick") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_KICK)
    channelMembersArgument("target")
    playerExecutor { player, args ->
        val user = player.user() ?: return@playerExecutor
        val channel: Channel? = channelService.getChannel(user)
        val target: Player by args

        if (channel == null) {
            player.sendText {
                error("Du bist in keinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        val userMember = user.channelMember(channel)
            ?: return@playerExecutor run {
                player.sendText {
                    error("Du bist in keinem Nachrichtenkanal.")
                }
            }

        if (!userMember.hasModeratorPermissions()) {
            user.sendText {
                error("Du verfügst nicht über die erforderliche Berechtigung.")
            }
            return@playerExecutor
        }

        val targetMember = target.user()?.channelMember(channel) ?: return@playerExecutor run {
            user.sendText {
                appendPrefix()
                error("Der Spieler ist nicht in deinem Nachrichtenkanal.")
            }
        }

        if (targetMember.hasModeratorPermissions()) {
            user.sendText {
                error("Du kannst diesen Spieler nicht aus dem Nachrichtenkanal entfernen.")
            }
            return@playerExecutor
        }


        channel.kick(targetMember)
        user.sendText {
            info("Du hast ")
            variableValue(targetMember.name)
            info(" aus dem Nachrichtenkanal ")
            variableValue(channel.channelName)
            info(" geworfen.")
        }

        target.sendText {
            info("Du wurdest aus dem Nachrichtenkanal ")
            variableValue(channel.channelName)
            info(" geworfen.")
        }
    }
}
