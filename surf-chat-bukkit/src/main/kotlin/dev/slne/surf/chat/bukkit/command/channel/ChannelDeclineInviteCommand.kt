package dev.slne.surf.chat.bukkit.command.channel

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.bukkit.command.argument.channelInviteArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelDeclineInviteCommand() = subcommand("decline") {
    channelInviteArgument("channel")
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_DECLINE)
    playerExecutor { player, args ->
        val channel: Channel by args
        val user = player.user() ?: return@playerExecutor

        plugin.launch {
            if (!channel.isInvited(user)) {
                player.sendText {
                    error("Du hast keine Einladung für den Nachrichtenkanal ")
                    variableValue(channel.channelName)
                    error(" erhalten.")
                }
                return@launch
            }

            channel.invite(user)

            player.sendText {
                info("Du hast die Einladung für den Nachrichtenkanal ")
                variableValue(channel.channelName)
                success(" abgelehnt.")
            }
        }
    }
}
