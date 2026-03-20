package dev.slne.surf.chat.paper.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object PermissionRegistry : PermissionRegistry() {
    private const val PREFIX = "surf.chat"
    private const val PREFIX_COMMAND = "$PREFIX.command"
    val PREFIX_TEAM = create("$PREFIX.team")

    val BYPASS_DISABLING = create("$PREFIX.disabling.bypass")
    val BYPASS_FILTER = create("$PREFIX.bypass.filter")
    val BYPASS_SPY = create("$PREFIX.bypass.spy")
    val BYPASS_FUNCTIONALITY = create("$PREFIX.bypass.functionality")

    val TEAM_NOTIFY_FUNCTIONALITY = create("$PREFIX_TEAM.notify.functionality")
    val TEAM_NOTIFY_DELETION = create("$PREFIX_TEAM.notify.deletion")

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
    val COMMAND_DENYLIST_ACTION_CLEAR = create("$PREFIX_COMMAND.denylist.action.clear")
    val COMMAND_DENYLIST_DEFAULTS = create("$PREFIX_COMMAND.denylist.defaults")
    val COMMAND_DENYLIST_CLEAR = create("$PREFIX_COMMAND.denylist.clear")

    val COMMAND_DIRECT_SPY = create("$PREFIX_COMMAND.direct-spy")
    val COMMAND_DIRECT_SPY_CLEAR = create("$PREFIX_COMMAND.direct-spy.clear")

    val COMMAND_TEAMCHAT = create("$PREFIX_COMMAND.teamchat")
}