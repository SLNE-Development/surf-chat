package dev.slne.surf.chat.core.client.permission

/**
 * Platform-neutral registry of all permission node strings used by surf-chat.
 */
object ChatPermissions {
    private const val PREFIX = "surf.chat"
    private const val PREFIX_COMMAND = "$PREFIX.command"
    const val PREFIX_TEAM = "$PREFIX.team"

    const val BYPASS_DISABLING = "$PREFIX.disabling.bypass"
    const val BYPASS_FILTER = "$PREFIX.bypass.filter"

    const val CONNECTION_MESSAGE_ALWAYS_SHOW = "$PREFIX.connection.always-show"
    const val BYPASS_SPY = "$PREFIX.bypass.spy"
    const val BYPASS_FUNCTIONALITY = "$PREFIX.bypass.functionality"

    const val SLOW_CHAT_BYPASS = "$PREFIX.slowchat.bypass"
    const val SLOW_CHAT_NOTIFY = "$PREFIX.slowchat.notify"
    const val SLOW_CHAT_COMMAND = "$PREFIX.slowchat.command"

    const val TEAM_NOTIFY_FUNCTIONALITY = "$PREFIX_TEAM.notify.functionality"
    const val TEAM_NOTIFY_DELETION = "$PREFIX_TEAM.notify.deletion"

    const val COMMAND_SURFCHAT = "$PREFIX_COMMAND.surfchat"
    const val COMMAND_SURFCHAT_RELOAD = "$PREFIX_COMMAND.surfchat.reload"
    const val COMMAND_SURFCHAT_DELETE = "$PREFIX_COMMAND.surfchat.delete"
    const val COMMAND_SURFCHAT_TELEPORT = "$PREFIX_COMMAND.surfchat.teleport"
    const val COMMAND_SURFCHAT_LOOKUP = "$PREFIX_COMMAND.surfchat.lookup"

    const val COMMAND_SURFCHAT_FUNCTIONALITY = "$PREFIX_COMMAND.surfchat.functionality"
    const val COMMAND_SURFCHAT_FUNCTIONALITY_TOGGLE =
        "$PREFIX_COMMAND.surfchat.functionality.toggle"
    const val COMMAND_SURFCHAT_FUNCTIONALITY_STATUS =
        "$PREFIX_COMMAND.surfchat.functionality.status"
    const val COMMAND_SURFCHAT_FUNCTIONALITY_LIST = "$PREFIX_COMMAND.surfchat.functionality.list"

    const val COMMAND_SURFCHAT_LOOKUP_HELP = "$PREFIX_COMMAND.surfchat.lookup.help"

    const val COMMAND_IGNORE = "$PREFIX_COMMAND.ignore"
    const val COMMAND_IGNORE_LIST = "$PREFIX_COMMAND.ignore.list"

    const val COMMAND_DIRECT_SPY = "$PREFIX_COMMAND.direct-spy"
    const val COMMAND_DIRECT_SPY_CLEAR = "$PREFIX_COMMAND.direct-spy.clear"

    const val COMMAND_TEAMCHAT = "$PREFIX_COMMAND.teamchat"

    const val COMMAND_PM = "$PREFIX_COMMAND.msg"
    const val COMMAND_REPLY = "$PREFIX_COMMAND.reply"
}
