package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry

fun channelCommand() = commandAPICommand("channel") {
    withPermission(SurfChatPermissionRegistry.COMMAND_CHANNEL)

    channelCreateCommand()
    channelDeleteCommand()
    channelJoinCommand()
    channelLeaveCommand()

    channelListCommand()
    channelMembersCommand()
    channelInfoCommand()

    channelInviteCommand()
    channelInviteAcceptCommand()
    channelInviteDeclineCommand()
    channelInviteRevokeCommand()

    channelPromoteCommand()
    channelDeleteCommand()

    channelKickCommand()
    channelBanCommand()
    channelUnBanCommand()
    channelTransferCommand()
    channelVisibilityCommand()
}