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

fun CommandAPICommand.channelInviteRevokeCommand() = subcommand("revoke") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_REVOKE)
    onlineCloudPlayerArgument("target")
    playerExecutor { player, args ->
        val user = player.cloudPlayer
        val channel: Channel = channelService.getChannel(user) ?: return@playerExecutor run {
            player.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
        }

        val target: CloudPlayer by args
        val userMember = user.channelMember(channel) ?: return@playerExecutor run {
            player.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
        }

        if (!userMember.hasModeratorPermissions()) {
            player.sendText {
                appendPrefix()
                error("Du hast keine Moderationsrechte in diesem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        if (!channel.isInvited(target)) {
            player.sendText {
                appendPrefix()
                info("Der Spieler ")
                variableValue(target.name)
                info(" hat keine Einladung für diesen Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        channel.revoke(target)

        player.sendText {
            appendPrefix()
            info("Du hast die Einladung des Spielers ")
            variableValue(target.name)
            info(" im Nachrichtenkanal ")
            variableValue(channel.channelName)
            info(" zurückgezogen.")
        }
        // TODO: surf-settings
//        plugin.launch {
//            if (target.configure().invitesEnabled()) {
//                target.sendText {
//                    appendPrefix()
//                    info("Deine Einladung in den Nachrichtenkanal ")
//                    variableValue(channel.channelName)
//                    info(" wurde zurückgezogen.")
//                }
//            }
//        }
    }
}
