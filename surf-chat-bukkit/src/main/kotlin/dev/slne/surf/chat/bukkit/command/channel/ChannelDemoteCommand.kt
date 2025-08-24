package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelRole
import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.bukkit.command.argument.channelMemberArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelDemoteCommand() = subcommand("demote") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_DEMOTE)
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
        val targetUser = target.user() ?: return@playerExecutor

        if (target.uuid == player.uniqueId) {
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

        if (!target.hasModeratorPermissions()) {
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

        channel.demote(target)

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
