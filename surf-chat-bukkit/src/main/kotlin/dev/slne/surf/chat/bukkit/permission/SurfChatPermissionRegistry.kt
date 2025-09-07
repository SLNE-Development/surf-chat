package dev.slne.surf.chat.bukkit.permission

import dev.slne.surf.chat.core.Constants
import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object SurfChatPermissionRegistry : PermissionRegistry() {
    private const val PREFIX = "surf.chat"
    private const val PREFIX_COMMAND = "$PREFIX.command"

    val FILTER_BYPASS = create("$PREFIX.filter.bypass")
    val TEAM_ACCESS = create(Constants.TEAM_PERMISSION)
    val AUTO_CHAT_DISABLING_BYPASS = create("$PREFIX.disabling.bypass")

    val COMMAND_SURFCHAT = create("$PREFIX_COMMAND.surfchat")
    val COMMAND_SURFCHAT_RELOAD = create("$PREFIX_COMMAND.surfchat.reload")
    val COMMAND_SURFCHAT_DELETE = create("$PREFIX_COMMAND.surfchat.delete")
    val COMMAND_SURFCHAT_TELEPORT = create("$PREFIX_COMMAND.surfchat.teleport")
    val COMMAND_SURFCHAT_LOOKUP = create("$PREFIX_COMMAND.surfchat.lookup")

    val COMMAND_SURFCHAT_FUNCTIONALITY = create("$PREFIX_COMMAND.surfchat.functionality")
    val COMMAND_SURFCHAT_FUNCTIONALITY_TOGGLE =
        create("$PREFIX_COMMAND.surfchat.functionality.toggle")
    val COMMAND_SURFCHAT_FUNCTIONALITY_STATUS =
        create("$PREFIX_COMMAND.surfchat.functionality.status")
    val COMMAND_SURFCHAT_FUNCTIONALITY_LIST = create("$PREFIX_COMMAND.surfchat.functionality.list")

    val COMMAND_SURFCHAT_LOOKUP_HELP = create("$PREFIX_COMMAND.surfchat.lookup.help")
    val COMMAND_IGNORE = create("$PREFIX_COMMAND.ignore")

    val COMMAND_IGNORE_LIST = create("$PREFIX_COMMAND.ignore.list")
    val COMMAND_DENYLIST = create("$PREFIX_COMMAND.denylist")
    val COMMAND_DENYLIST_ADD = create("$PREFIX_COMMAND.denylist.add")
    val COMMAND_DENYLIST_REMOVE = create("$PREFIX_COMMAND.denylist.remove")
    val COMMAND_DENYLIST_LIST = create("$PREFIX_COMMAND.denylist.list")
    val COMMAND_DENYLIST_FETCH = create("$PREFIX_COMMAND.denylist.fetch")

    val COMMAND_DENYLIST_ACTION = create("$PREFIX_COMMAND.denylist.action")
    val COMMAND_DENYLIST_ACTION_ADD = create("$PREFIX_COMMAND.denylist.action.add")
    val COMMAND_DENYLIST_ACTION_REMOVE = create("$PREFIX_COMMAND.denylist.action.remove")
    val COMMAND_DENYLIST_ACTION_LIST = create("$PREFIX_COMMAND.denylist.action.list")
    val COMMAND_DENYLIST_ACTION_FETCH = create("$PREFIX_COMMAND.denylist.action.fetch")

    val COMMAND_DIRECT_SPY = create("$PREFIX_COMMAND.direct-spy")
    val COMMAND_DIRECT_SPY_CLEAR = create("$PREFIX_COMMAND.direct-spy.clear")

    val COMMAND_CHANNEL = create("$PREFIX_COMMAND.channel")
    val COMMAND_CHANNEL_ACCEPT = create("$PREFIX_COMMAND.channel.accept")
    val COMMAND_CHANNEL_CREATE = create("$PREFIX_COMMAND.channel.create")
    val COMMAND_CHANNEL_DELETE = create("$PREFIX_COMMAND.channel.delete")
    val COMMAND_CHANNEL_LIST = create("$PREFIX_COMMAND.channel.list")
    val COMMAND_CHANNEL_INFO = create("$PREFIX_COMMAND.channel.info")
    val COMMAND_CHANNEL_BAN = create("$PREFIX_COMMAND.channel.ban")
    val COMMAND_CHANNEL_UNBAN = create("$PREFIX_COMMAND.channel.unban")
    val COMMAND_CHANNEL_KICK = create("$PREFIX_COMMAND.channel.kick")
    val COMMAND_CHANNEL_JOIN = create("$PREFIX_COMMAND.channel.join")
    val COMMAND_CHANNEL_LEAVE = create("$PREFIX_COMMAND.channel.leave")
    val COMMAND_CHANNEL_PROMOTE = create("$PREFIX_COMMAND.channel.promote")
    val COMMAND_CHANNEL_DEMOTE = create("$PREFIX_COMMAND.channel.demote")
    val COMMAND_CHANNEL_TRANSFER = create("$PREFIX_COMMAND.channel.transfer")
    val COMMAND_CHANNEL_INVITE = create("$PREFIX_COMMAND.channel.invite")
    val COMMAND_CHANNEL_REVOKE = create("$PREFIX_COMMAND.channel.revoke")
    val COMMAND_CHANNEL_DECLINE = create("$PREFIX_COMMAND.channel.deny")
    val COMMAND_CHANNEL_MEMBERS = create("$PREFIX_COMMAND.channel.members")

    val COMMAND_CHANNEL_VISIBILITY = create("$PREFIX_COMMAND.channel.mode")
    val COMMAND_CHANNEL_ADMIN = create("$PREFIX_COMMAND.channel.admin")
    val COMMAND_CHANNEL_ADMIN_MOVE = create("$PREFIX_COMMAND.channel.admin.move")
    val COMMAND_CHANNEL_ADMIN_JOIN = create("$PREFIX_COMMAND.channel.admin.join")
    val COMMAND_CHANNEL_ADMIN_DELETE = create("$PREFIX_COMMAND.channel.admin.delete")
    val COMMAND_CHANNEL_ADMIN_SPY = create("$PREFIX_COMMAND.channel.admin.spy")

    val COMMAND_CHANNEL_ADMIN_SPY_CLEAR = create("$PREFIX_COMMAND.channel.admin.spy.clear")
    val COMMAND_SETTINGS = create("$PREFIX_COMMAND.settings")
    val COMMAND_SETTINGS_PING = create("$PREFIX_COMMAND.settings.ping")
    val COMMAND_SETTINGS_INVITES = create("$PREFIX_COMMAND.settings.invites")

    val COMMAND_SETTINGS_DIRECT_MESSAGES = create("$PREFIX_COMMAND.settings.direct-messages")

    val COMMAND_TEAMCHAT = create("$PREFIX_COMMAND.teamchat")
}