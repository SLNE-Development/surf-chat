package dev.slne.surf.chat.paper.channel.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.paper.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.paper.plugin

fun channelCommand() = commandAPICommand("channel", plugin) {
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

    channelKickCommand()
    channelBanCommand()
    channelUnBanCommand()
    channelTransferCommand()
    channelVisibilityCommand()
}