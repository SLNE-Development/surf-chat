package dev.slne.surf.chat.paper.channel.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelMember
import dev.slne.surf.chat.paper.channel.argument.channelMemberArgument
import dev.slne.surf.chat.paper.channel.channelService
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.channelMember
import dev.slne.surf.chat.paper.util.sendText
import dev.slne.surf.cloud.api.common.player.toCloudPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelBanCommand() = subcommand("ban") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_BAN)
    channelMemberArgument("member")
    playerExecutor { player, args ->
        val user = player.toCloudPlayer() ?: return@playerExecutor
        val channel: Channel = channelService.getChannel(user) ?: run {
            player.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }
        val member: ChannelMember by args

        plugin.launch {
            val userMember = user.channelMember(channel) ?: run {
                player.sendText {
                    appendPrefix()
                    error("Du bist nicht in diesem Nachrichtenkanal.")
                }
                return@launch
            }

            if (!userMember.hasModeratorPermissions()) {
                player.sendText {
                    appendPrefix()
                    error("Du verfügst nicht über die erforderliche Berechtigung.")
                }
                return@launch
            }

            if (member.hasModeratorPermissions()) {
                player.sendText {
                    appendPrefix()
                    error("Du kannst diesen Spieler nicht verbannen.")
                }
                return@launch
            }

            channel.ban(member.cloudPlayer)

            player.sendText {
                appendPrefix()
                success("Du hast ")
                variableValue(member.name)
                success(" aus dem Nachrichtenkanal ")
                variableValue(channel.channelName)
                success(" verbannt.")
            }

            member.sendText {
                appendPrefix()
                info("Du wurdest aus dem Nachrichtenkanal ")
                variableValue(channel.channelName)
                info(" verbannt.")
            }
        }
    }
}
