package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry
import dev.slne.surf.chat.bukkit.plugin

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