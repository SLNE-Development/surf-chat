package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.model.Channel
import dev.slne.surf.chat.bukkit.command.argument.channelArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.util.user
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelForceJoinCommand() = subcommand("join") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_ADMIN_JOIN)
    channelArgument("channel")
    playerExecutor { player, args ->
        val channel: Channel by args
        val user = player.user() ?: return@playerExecutor

        if (channelService.getChannel(user) != null) {
            player.sendText {
                appendPrefix()
                error("Du bist bereits in einem Nachrichtenkanal.")
            }
            return@playerExecutor
        }

        channel.addMember(user)

        player.sendText {
            appendPrefix()
            success("Du bist dem Nachrichtenkanal ")
            variableValue(channel.channelName)
            success(" beigetreten.")
        }
    }
}
