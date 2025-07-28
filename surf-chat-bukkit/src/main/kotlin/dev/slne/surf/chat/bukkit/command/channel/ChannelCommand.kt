package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry

fun channelCommand() = commandAPICommand("channel") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL)

    channelCreateCommand()
    channelBanCommand()
    channelInviteAcceptCommand()
    channelInviteDeclineCommand()
}