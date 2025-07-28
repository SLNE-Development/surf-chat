package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.components
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun CommandAPICommand.channelInviteCommand() = subcommand("invite") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_INVITE)
    playerArgument("target")
    playerExecutor { player, args ->
        val user = player.user() ?: return@playerExecutor
        val channel: Channel = channelService.getChannel(user) ?: run {
            player.sendText {
                error("Du bist in keinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        val target: Player by args
        val targetUser = target.user() ?: return@playerExecutor

        if (channel.isInvited(targetUser)) {
            user.sendText {
                error("Der Spieler ")
                variableValue(target.name)
                error(" wurde bereits eingeladen.")
            }
            return@playerExecutor
        }

        if (channel.isMember(targetUser)) {
            user.sendText {
                error("Der Spieler ")
                variableValue(target.name)
                error(" ist bereits in diesem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        val userMember = user.channelMember(channel) ?: run {
            user.sendText {
                error("Du bist in keinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        if (!userMember.hasModeratorPermissions()) {
            user.sendText {
                error("Du verfügst nicht über die erforderliche Berechtigung.")
            }
            return@playerExecutor
        }

        channel.invite(targetUser)

        user.sendText {
            info("Du hast ")
            variableValue(target.name)
            info(" in den Nachrichtenkanal ")
            variableValue(channel.channelName)
            info(" eingeladen.")
        }

        plugin.launch {
            if (targetUser.configure().invitesEnabled()) {
                target.sendText {
                    appendPrefix()
                    info("Du wurdest in den Nachrichtenkanal ")
                    variableValue(channel.channelName)
                    info(" eingeladen. ")

                    append(components.inviteAccept(channel))
                    append(components.inviteDecline(channel))
                }
            }
        }
    }
}
