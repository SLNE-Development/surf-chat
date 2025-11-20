package dev.slne.surf.chat.paper.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.chat.paper.command.argument.userArgument
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.appendInviteAccept
import dev.slne.surf.chat.paper.util.appendInviteDecline
import dev.slne.surf.chat.paper.util.sendText
import dev.slne.surf.chat.paper.util.user
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlinx.coroutines.Dispatchers

fun CommandAPICommand.channelInviteCommand() = subcommand("invite") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_INVITE)
    userArgument("target")
    playerExecutor { player, args ->
        val user = player.user() ?: return@playerExecutor
        val channel: Channel = channelService.getChannel(user) ?: run {
            player.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        val target: User by args

        if (channel.isInvited(target)) {
            user.sendText {
                appendPrefix()
                error("Der Spieler ")
                variableValue(target.name)
                error(" wurde bereits eingeladen.")
            }
            return@playerExecutor
        }

        if (channel.isMember(target)) {
            user.sendText {
                appendPrefix()
                error("Der Spieler ")
                variableValue(target.name)
                error(" ist bereits in diesem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        val userMember = user.channelMember(channel) ?: run {
            user.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
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

        channel.invite(target)

        user.sendText {
            appendPrefix()
            info("Du hast ")
            variableValue(target.name)
            info(" in den Nachrichtenkanal ")
            variableValue(channel.channelName)
            info(" eingeladen.")
        }

        plugin.launch(Dispatchers.IO) {
            if (target.configure().invitesEnabled()) {
                target.sendText {
                    appendPrefix()
                    info("Du wurdest in den Nachrichtenkanal ")
                    variableValue(channel.channelName)
                    info(" eingeladen. ")

                    appendInviteAccept(channel)
                    appendInviteDecline(channel)
                }
            }
        }
    }
}
