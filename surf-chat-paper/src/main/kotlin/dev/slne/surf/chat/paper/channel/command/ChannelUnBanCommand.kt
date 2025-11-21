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

fun CommandAPICommand.channelUnBanCommand() = subcommand("unban") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_UNBAN)
    onlineCloudPlayerArgument("target")
    playerExecutor { player, args ->
        val user = player.cloudPlayer
        val target: CloudPlayer by args
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
