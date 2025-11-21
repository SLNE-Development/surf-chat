package dev.slne.surf.chat.paper.channel.command

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.paper.channel.argument.channelInviteArgument
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.plugin
import dev.slne.surf.chat.paper.util.cloudPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelInviteDeclineCommand() = subcommand("decline") {
    channelInviteArgument("channel")
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_DECLINE)
    playerExecutor { player, args ->
        val channel: Channel by args
        val user = player.cloudPlayer

        plugin.launch {
            if (!channel.isInvited(user)) {
                player.sendText {
                    appendPrefix()
                    error("Du hast keine Einladung für den Nachrichtenkanal ")
                    variableValue(channel.channelName)
                    error(" erhalten.")
                }
                return@launch
            }

            channel.invitedPlayers.remove(user.uuid)

            player.sendText {
                appendPrefix()
                info("Du hast die Einladung für den Nachrichtenkanal ")
                variableValue(channel.channelName)
                success(" abgelehnt.")
            }
        }
    }
}
