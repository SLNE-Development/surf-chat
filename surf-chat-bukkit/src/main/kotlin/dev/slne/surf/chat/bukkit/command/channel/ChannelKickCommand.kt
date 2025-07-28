package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.bukkit.command.argument.channelMemberArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelKickCommand() = subcommand("kick") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_KICK)
    channelMemberArgument("target")
    playerExecutor { player, args ->
        val user = player.user() ?: return@playerExecutor
        val channel: Channel? = channelService.getChannel(user)
        val target: ChannelMember by args

        if (channel == null) {
            player.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        val userMember = user.channelMember(channel)
            ?: return@playerExecutor run {
                player.sendText {
                    appendPrefix()
                    error("Du bist in keinem Nachrichtenkanal.")
                }
            }

        if (!userMember.hasModeratorPermissions()) {
            user.sendText {
                appendPrefix()
                error("Du verfügst nicht über die erforderliche Berechtigung.")
            }
            return@playerExecutor
        }

        if (target.hasModeratorPermissions()) {
            user.sendText {
                appendPrefix()
                error("Du kannst diesen Spieler nicht aus dem Nachrichtenkanal entfernen.")
            }
            return@playerExecutor
        }


        channel.kick(target)
        channel.sendText {
            appendPrefix()
            variableValue(user.name)
            info(" hat den Nachrichtenkanal verlassen.")
        }

        user.sendText {
            appendPrefix()
            info("Du hast ")
            variableValue(target.name)
            info(" aus dem Nachrichtenkanal ")
            variableValue(channel.channelName)
            info(" geworfen.")
        }

        target.sendText {
            appendPrefix()
            info("Du wurdest aus dem Nachrichtenkanal ")
            variableValue(channel.channelName)
            info(" geworfen.")
        }
    }
}
