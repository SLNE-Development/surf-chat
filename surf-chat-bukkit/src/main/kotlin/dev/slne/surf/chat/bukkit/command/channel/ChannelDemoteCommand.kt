package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.api.model.ChannelRole
import dev.slne.surf.chat.bukkit.command.argument.channelMembersArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun CommandAPICommand.channelDemoteCommand() = subcommand("demote") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_DEMOTE)
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
        val targetUser = target.user() ?: return@playerExecutor
        val userMember = user.channelMember(channel) ?: run {
            player.sendText {
                appendPrefix()
                error("Du bist kein Mitglied in dem Nachrichtenkanal.")
            }
            return@playerExecutor
        }
        val targetMember = targetUser.channelMember(channel) ?: run {
            player.sendText {
                appendPrefix()
                error("Der Spieler ")
                variableValue(target.name)
                error(" ist kein Mitglied in dem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        if (target.uniqueId == player.uniqueId) {
            player.sendText {
                appendPrefix()
                error("Du kannst dich nicht selbst degradieren.")
            }
            return@playerExecutor
        }

        if (!channel.isOwner(user)) {
            player.sendText {
                appendPrefix()
                error("Du verfügst nicht über die erforderliche Berechtigung.")
            }
            return@playerExecutor
        }

        if (!channel.isMember(targetUser)) {
            player.sendText {
                appendPrefix()
                error("Der Spieler ")
                variableValue(target.name)
                error(" ist kein Mitglied in dem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        if (!targetMember.hasModeratorPermissions()) {
            player.sendText {
                appendPrefix()
                error("Der Spieler ")
                variableValue(target.name)
                error(" ist bereits ein Mitglied.")
            }
            return@playerExecutor
        }

        if (channel.members.none { it.role == ChannelRole.OWNER }) {
            player.sendText {
                appendPrefix()
                error("Der Nachrichtenkanal benötigt mindestens einen Besitzer.")
            }
            return@playerExecutor
        }

        channel.demote(targetMember)

        player.sendText {
            appendPrefix()
            success("Du hast ")
            variableValue(target.name)
            success(" degradiert.")
        }

        target.sendText {
            appendPrefix()
            info("Du wurdest degradiert.")
        }
    }
}
