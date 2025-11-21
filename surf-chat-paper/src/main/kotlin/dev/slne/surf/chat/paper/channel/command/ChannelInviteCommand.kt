package dev.slne.surf.chat.paper.channel.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.paper.channel.channelService
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.util.channelMember
import dev.slne.surf.chat.paper.util.cloudPlayer
import dev.slne.surf.cloud.api.client.paper.command.args.onlineCloudPlayerArgument
import dev.slne.surf.cloud.api.common.player.CloudPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelInviteCommand() = subcommand("invite") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_INVITE)
    onlineCloudPlayerArgument("target")
    playerExecutor { player, args ->
        val user = player.cloudPlayer
        val channel: Channel = channelService.getChannel(user) ?: run {
            player.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        val target: CloudPlayer by args

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

        //TODO: surf-settings
//        plugin.launch(Dispatchers.IO) {
//            if (target.configure().invitesEnabled()) {
//                target.sendText {
//                    appendPrefix()
//                    info("Du wurdest in den Nachrichtenkanal ")
//                    variableValue(channel.channelName)
//                    info(" eingeladen. ")
//
//                    appendInviteAccept(channel)
//                    appendInviteDecline(channel)
//                }
//            }
//        }
    }
}
