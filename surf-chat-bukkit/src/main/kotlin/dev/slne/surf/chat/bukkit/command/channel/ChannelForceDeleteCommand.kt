package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.bukkit.command.argument.channelArgument
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.core.service.channelService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun CommandAPICommand.channelForceDeleteCommand() = subcommand("delete") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL_ADMIN_DELETE)
    channelArgument("channel")
    playerExecutor { player, args ->
        val channel: Channel by args

        channelService.deleteChannel(channel)

        player.sendText {
            appendPrefix()
            success("Der Nachrichtenkanal ")
            variableValue(channel.channelName)
            success(" wurde gelöscht.")
        }
    }
}
