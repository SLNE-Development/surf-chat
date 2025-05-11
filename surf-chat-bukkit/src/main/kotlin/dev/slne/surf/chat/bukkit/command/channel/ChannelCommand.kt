package dev.slne.surf.chat.bukkit.command.channel

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.chat.bukkit.command.channel.*

class ChannelCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission("surf.chat.command.channel")
        subcommand(ChannelAcceptInviteCommand("accept"))
        subcommand(ChannelCreateCommand("create"))
        subcommand(ChannelDeleteCommand("delete"))
        subcommand(ChannelInviteCommand("invite"))
        subcommand(ChannelInviteRevokeCommand("revoke"))
        subcommand(ChannelInfoCommand("info"))
        subcommand(ChannelMembersCommand("members"))
        subcommand(ChannelListCommand("list"))
        subcommand(ChannelJoinCommand("join"))
        subcommand(ChannelLeaveCommand("leave"))
        subcommand(ChannelPrivacyModeCommand("visibility"))
        subcommand(ChannelBanCommand("ban"))
        subcommand(ChannelUnBanCommand("unban"))
        subcommand(ChannelDemoteCommand("demote"))
        subcommand(ChannelPromoteCommand("promote"))
        subcommand(ChannelKickCommand("kick"))
        subcommand(ChannelTransferOwnerShipCommand("transferOwnership"))
    }
}
