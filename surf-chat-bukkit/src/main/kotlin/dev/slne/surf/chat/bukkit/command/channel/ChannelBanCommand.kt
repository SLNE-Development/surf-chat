package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.bukkit.command.argument.channelMemberArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelBanCommand() = subcommand("ban") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_BAN)
    channelMemberArgument("member")
    playerExecutor { player, args ->
        val user = player.user() ?: return@playerExecutor
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

            channel.ban(member.user() ?: run {
                player.sendText {
                    appendPrefix()
                    error("Der Spieler ist nicht online oder existiert nicht.")
                }
                return@launch
            })

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
