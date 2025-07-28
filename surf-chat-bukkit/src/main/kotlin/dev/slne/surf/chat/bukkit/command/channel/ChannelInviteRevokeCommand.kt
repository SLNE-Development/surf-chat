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
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun CommandAPICommand.channelInviteRevokeCommand() = subcommand("revoke") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_REVOKE)
    playerArgument("player")
    playerExecutor { player, args ->
        val user = player.user() ?: return@playerExecutor
        val channel: Channel = channelService.getChannel(user) ?: return@playerExecutor run {
            player.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
        }

        val target: Player by args
        val targetUser = target.user() ?: return@playerExecutor run {
            player.sendText {
                appendPrefix()
                error("Der Spieler ")
                variableValue(target.name)
                error(" ist nicht im System registriert.")
            }
        }
        val userMember = user.channelMember(channel) ?: return@playerExecutor run {
            player.sendText {
                appendPrefix()
                error("Du bist in keinem Nachrichtenkanal.")
            }
        }

        if (!userMember.hasModeratorPermissions()) {
            player.sendText {
                error("Du hast keine Moderationsrechte in diesem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        if (!channel.isInvited(targetUser)) {
            player.sendText {
                info("Der Spieler ")
                variableValue(targetUser.name)
                info(" hat keine Einladung für diesen Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        channel.revoke(targetUser)

        player.sendText {
            info("Du hast die Einladung des Spielers ")
            variableValue(targetUser.name)
            info(" im Nachrichtenkanal ")
            variableValue(channel.channelName)
            info(" zurückgezogen.")
        }

        plugin.launch {
            if (targetUser.configure().invitesEnabled()) {
                target.sendText {
                    appendPrefix()
                    info("Deine Einladung in den Nachrichtenkanal ")
                    variableValue(channel.channelName)
                    info(" wurde zurückgezogen.")
                }
            }
        }
    }
}
