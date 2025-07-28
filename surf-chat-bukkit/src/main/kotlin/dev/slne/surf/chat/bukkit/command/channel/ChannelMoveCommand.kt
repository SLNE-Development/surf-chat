package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.bukkit.command.argument.channelArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.sendText
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Player

fun CommandAPICommand.channelMoveCommand() = subcommand("move") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_ADMIN_MOVE)
    playerArgument("target")
    channelArgument("channel")
    playerExecutor { player, args ->
        val target: Player by args
        val channel: Channel by args

        val targetUser = target.user() ?: run {
            player.sendText {
                appendPrefix()
                error("Der Spieler ist nicht online oder existiert nicht.")
            }
            return@playerExecutor
        }

        channelService.move(targetUser, channel)

        player.sendText {
            appendPrefix()
            success("Du hast ")
            variableValue(targetUser.name)
            success(" in den Nachrichtenkanal ")
            variableValue(channel.channelName)
            success(" verschoben.")
        }

        targetUser.sendText {
            appendPrefix()
            info("Du wurdest in den Nachrichtenkanal ")
            variableValue(channel.channelName)
            info(" verschoben.")
        }
    }
}
