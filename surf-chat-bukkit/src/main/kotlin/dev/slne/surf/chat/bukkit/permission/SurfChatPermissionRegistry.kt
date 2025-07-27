package dev.slne.surf.chat.bukkit.permission

import dev.slne.surf.chat.core.Constants
import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object SurfChatPermissionRegistry : PermissionRegistry() {
    private const val PREFIX = "surf.chat"
    private const val PREFIX_COMMAND = "$PREFIX.command"

    val FILTER_BYPASS = create("$PREFIX.filter.bypass")
    val TEAM_ACCESS = create(Constants.TEAM_PERMISSION)

    val COMMAND_SURFCHAT = create("$PREFIX_COMMAND.surfchat")
    val COMMAND_SURFCHAT_DELETE = create("$PREFIX_COMMAND.surfchat.delete")
    val COMMAND_SURFCHAT_TELEPORT = create("$PREFIX_COMMAND.surfchat.teleport")
    val COMMAND_SURFCHAT_LOOKUP = create("$PREFIX_COMMAND.surfchat.lookup")

    val COMMAND_CHANNEL = create("$PREFIX_COMMAND.channel")
    val COMMAND_CHANNEL_ACCEPT = create("$PREFIX_COMMAND.channel.accept")
    val COMMAND_CHANNEL_CREATE = create("surf.chat.command.channel.create")
    val COMMAND_CHANNEL_DELETE = create("surf.chat.command.channel.delete")
    val COMMAND_CHANNEL_LIST = create("surf.chat.command.channel.list")
    val COMMAND_CHANNEL_INFO = create("surf.chat.command.channel.info")
    val COMMAND_CHANNEL_BAN = create("surf.chat.command.channel.ban")
    val COMMAND_CHANNEL_UNBAN = create("surf.chat.command.channel.unban")
    val COMMAND_CHANNEL_KICK = create("surf.chat.command.channel.kick")
    val COMMAND_CHANNEL_JOIN = create("surf.chat.command.channel.join")
    val COMMAND_CHANNEL_LEAVE = create("surf.chat.command.channel.leave")
    val COMMAND_CHANNEL_PROMOTE = create("surf.chat.command.channel.promote")
    val COMMAND_CHANNEL_DEMOTE = create("surf.chat.command.channel.demote")
    val COMMAND_CHANNEL_TRANSFER = create("surf.chat.command.channel.transfer")
    val COMMAND_CHANNEL_INVITE = create("surf.chat.command.channel.invite")
    val COMMAND_CHANNEL_REVOKE = create("surf.chat.command.channel.uninvite")
    val COMMAND_CHANNEL_DECLINE = create("surf.chat.command.channel.deny")
    val COMMAND_CHANNEL_MEMBERS = create("surf.chat.command.channel.members")
    val COMMAND_CHANNEL_MODE = create("surf.chat.command.channel.mode")

    val COMMAND_CHANNEL_ADMIN = create("surf.chat.command.channel.admin")
    val COMMAND_CHANNEL_ADMIN_MOVE = create("surf.chat.command.channel.admin.move")
    val COMMAND_CHANNEL_ADMIN_JOIN = create("surf.chat.command.channel.admin.join")
    val COMMAND_CHANNEL_ADMIN_DELETE = create("surf.chat.command.channel.admin.delete")
    val COMMAND_CHANNEL_ADMIN_SPY = create("surf.chat.command.channel.admin.spy")

    val COMMAND_SETTINGS = create("$PREFIX_COMMAND.settings")
    val COMMAND_SETTINGS_PING = create("$PREFIX_COMMAND.settings.ping")
    val COMMAND_SETTINGS_DIRECT_MESSAGES = create("$PREFIX_COMMAND.settings.direct-messages")

    val COMMAND_TEAMCHAT = create("$PREFIX_COMMAND.teamchat")
}