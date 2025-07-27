package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.bukkit.command.argument.channelMembersArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun CommandAPICommand.channelBanCommand() = subcommand("ban") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_BAN)
    channelMembersArgument("player")
    playerExecutor { player, args ->
        val user = player.user() ?: return@playerExecutor
        val channel: Channel = channelService.getChannel(user) ?: run {
            player.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
            return@playerExecutor
        }
        val target = args.getUnchecked<Player>("player") ?: return@playerExecutor

        plugin.launch {
            val targetUser = target.user() ?: run {
                target.sendText {
                    appendPrefix()
                    error("Der Spieler ist nicht online.")
                }
                return@launch
            }

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
            }

            val targetMember = targetUser.channelMember(channel) ?: run {
                target.sendText {
                    appendPrefix()
                    error("Der Spieler ist nicht in diesem Nachrichtenkanal.")
                }
                return@launch
            }

            if (targetMember.hasModeratorPermissions()) {
                player.sendText {
                    appendPrefix()
                    error("Du kannst diesen Spieler nicht verbannen.")
                }
                return@launch
            }

            channel.ban(targetUser)

            player.sendText {
                success("Du hast ")
                variableValue(target.name)
                success(" aus dem Nachrichtenkanal ")
                variableValue(channel.channelName)
                success(" verbannt.")
            }
            target.sendText {
                info("Du wurdest aus dem Nachrichtenkanal ")
                variableValue(channel.channelName)
                info(" verbannt.")
            }
        }
    }
}
