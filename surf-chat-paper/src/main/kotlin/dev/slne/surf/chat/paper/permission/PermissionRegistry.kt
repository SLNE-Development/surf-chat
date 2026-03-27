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

    val COMMAND_DIRECT_SPY = create("$PREFIX_COMMAND.direct-spy")
    val COMMAND_DIRECT_SPY_CLEAR = create("$PREFIX_COMMAND.direct-spy.clear")

    val COMMAND_TEAMCHAT = create("$PREFIX_COMMAND.teamchat")

    val COMMAND_PM = create("$PREFIX_COMMAND.msg")
    val COMMAND_REPLY = create("$PREFIX_COMMAND.reply")
}