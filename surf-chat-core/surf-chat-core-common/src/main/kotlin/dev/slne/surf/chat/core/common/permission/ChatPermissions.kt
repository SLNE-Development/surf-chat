package dev.slne.surf.chat.core.common.permission

object ChatPermissions {
    private const val BASE = "surf.chat"
    private const val COMMAND = "$BASE.command"
    private const val BYPASS = "$BASE.team"

    const val AUTO_CHAT_DISABLING_BYPASS = "$BASE.disabling.bypass"

    const val TEAM_NOTIFY = "$BASE.team.notify"

    const val TEAM_BYPASS_FILTER = "$BYPASS.filter"
    const val TEAM_BYPASS_SPY = "$BYPASS.spy"
    const val TEAM_BYPASS_FUNCTIONALITY = "$BYPASS.functionality"
    const val TEAM_NOTIFY_FUNCTIONALITY = "$BYPASS.functionality.notify"
    const val TEAM_NOTIFY_DELETION = "$BYPASS.deletion"

    const val COMMAND_SURFCHAT = "$COMMAND.surfchat"
    const val COMMAND_SURFCHAT_RELOAD = "$COMMAND.surfchat.reload"
    const val COMMAND_SURFCHAT_DELETE = "$COMMAND.surfchat.delete"
    const val COMMAND_SURFCHAT_TELEPORT = "$COMMAND.surfchat.teleport"
    const val COMMAND_SURFCHAT_LOOKUP = "$COMMAND.surfchat.lookup"

    const val COMMAND_SURFCHAT_FUNCTIONALITY = "$COMMAND.surfchat.functionality"
    const val COMMAND_SURFCHAT_FUNCTIONALITY_TOGGLE = "$COMMAND.surfchat.functionality.toggle"
    const val COMMAND_SURFCHAT_FUNCTIONALITY_STATUS = "$COMMAND.surfchat.functionality.status"
    const val COMMAND_SURFCHAT_FUNCTIONALITY_LIST = "$COMMAND.surfchat.functionality.list"

    const val COMMAND_SURFCHAT_LOOKUP_HELP = "$COMMAND.surfchat.lookup.help"

    const val COMMAND_IGNORE = "$COMMAND.ignore"
    const val COMMAND_IGNORE_LIST = "$COMMAND.ignore.list"

    const val COMMAND_TEAMCHAT = "$COMMAND.teamchat"

    const val COMMAND_DENYLIST = "$COMMAND.denylist"
    const val COMMAND_DENYLIST_ADD = "$COMMAND.denylist.add"
    const val COMMAND_DENYLIST_REMOVE = "$COMMAND.denylist.remove"
    const val COMMAND_DENYLIST_LIST = "$COMMAND.denylist.list"
    const val COMMAND_DENYLIST_FETCH = "$COMMAND.denylist.fetch"

    const val COMMAND_DENYLIST_ACTION = "$COMMAND.denylist.action"
    const val COMMAND_DENYLIST_ACTION_ADD = "$COMMAND.denylist.action.add"
    const val COMMAND_DENYLIST_ACTION_REMOVE = "$COMMAND.denylist.action.remove"
    const val COMMAND_DENYLIST_ACTION_LIST = "$COMMAND.denylist.action.list"
    const val COMMAND_DENYLIST_ACTION_FETCH = "$COMMAND.denylist.action.fetch"
    const val COMMAND_DENYLIST_ACTION_CLEAR = "$COMMAND.denylist.action.clear"
    const val COMMAND_DENYLIST_DEFAULTS = "$COMMAND.denylist.defaults"
    const val COMMAND_DENYLIST_CLEAR = "$COMMAND.denylist.clear"

    const val COMMAND_DIRECT_SPY = "$COMMAND.direct-spy"
    const val COMMAND_DIRECT_SPY_CLEAR = "$COMMAND.direct-spy.clear"

    const val COMMAND_CHANNEL = "$COMMAND.channel"
    const val COMMAND_CHANNEL_ACCEPT = "$COMMAND.channel.accept"
    const val COMMAND_CHANNEL_CREATE = "$COMMAND.channel.create"
    const val COMMAND_CHANNEL_DELETE = "$COMMAND.channel.delete"
    const val COMMAND_CHANNEL_LIST = "$COMMAND.channel.list"
    const val COMMAND_CHANNEL_INFO = "$COMMAND.channel.info"
    const val COMMAND_CHANNEL_BAN = "$COMMAND.channel.ban"
    const val COMMAND_CHANNEL_UNBAN = "$COMMAND.channel.unban"
    const val COMMAND_CHANNEL_KICK = "$COMMAND.channel.kick"
    const val COMMAND_CHANNEL_JOIN = "$COMMAND.channel.join"
    const val COMMAND_CHANNEL_LEAVE = "$COMMAND.channel.leave"
    const val COMMAND_CHANNEL_PROMOTE = "$COMMAND.channel.promote"
    const val COMMAND_CHANNEL_DEMOTE = "$COMMAND.channel.demote"
    const val COMMAND_CHANNEL_TRANSFER = "$COMMAND.channel.transfer"
    const val COMMAND_CHANNEL_INVITE = "$COMMAND.channel.invite"
    const val COMMAND_CHANNEL_REVOKE = "$COMMAND.channel.revoke"
    const val COMMAND_CHANNEL_DECLINE = "$COMMAND.channel.deny"
    const val COMMAND_CHANNEL_MEMBERS = "$COMMAND.channel.members"

    const val COMMAND_CHANNEL_VISIBILITY = "$COMMAND.channel.mode"

    const val COMMAND_CHANNEL_ADMIN = "$COMMAND.channel.admin"
    const val COMMAND_CHANNEL_ADMIN_MOVE = "$COMMAND.channel.admin.move"
    const val COMMAND_CHANNEL_ADMIN_JOIN = "$COMMAND.channel.admin.join"
    const val COMMAND_CHANNEL_ADMIN_DELETE = "$COMMAND.channel.admin.delete"
    const val COMMAND_CHANNEL_ADMIN_SPY = "$COMMAND.channel.admin.spy"
    const val COMMAND_CHANNEL_ADMIN_SPY_CLEAR = "$COMMAND.channel.admin.spy.clear"
    
    const val COMMAND_TELL = "$COMMAND.tell"
}
