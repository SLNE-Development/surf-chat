package dev.slne.surf.chat.paper.permission

import dev.slne.surf.chat.core.client.permission.ChatPermissions

object PermissionRegistry : dev.slne.surf.api.paper.permission.PermissionRegistry() {
    val PREFIX_TEAM = create(ChatPermissions.PREFIX_TEAM)

    val BYPASS_DISABLING = create(ChatPermissions.BYPASS_DISABLING)
    val BYPASS_FILTER = create(ChatPermissions.BYPASS_FILTER)

    val CONNECTION_MESSAGE_ALWAYS_SHOW = create(ChatPermissions.CONNECTION_MESSAGE_ALWAYS_SHOW)
    val BYPASS_SPY = create(ChatPermissions.BYPASS_SPY)
    val BYPASS_FUNCTIONALITY = create(ChatPermissions.BYPASS_FUNCTIONALITY)

    val SLOW_CHAT_BYPASS = create(ChatPermissions.SLOW_CHAT_BYPASS)
    val SLOW_CHAT_NOTIFY = create(ChatPermissions.SLOW_CHAT_NOTIFY)
    val SLOW_CHAT_COMMAND = create(ChatPermissions.SLOW_CHAT_COMMAND)

    val TEAM_NOTIFY_FUNCTIONALITY = create(ChatPermissions.TEAM_NOTIFY_FUNCTIONALITY)
    val TEAM_NOTIFY_DELETION = create(ChatPermissions.TEAM_NOTIFY_DELETION)

    val COMMAND_SURFCHAT = create(ChatPermissions.COMMAND_SURFCHAT)
    val COMMAND_SURFCHAT_RELOAD = create(ChatPermissions.COMMAND_SURFCHAT_RELOAD)
    val COMMAND_SURFCHAT_DELETE = create(ChatPermissions.COMMAND_SURFCHAT_DELETE)
    val COMMAND_SURFCHAT_TELEPORT = create(ChatPermissions.COMMAND_SURFCHAT_TELEPORT)
    val COMMAND_SURFCHAT_LOOKUP = create(ChatPermissions.COMMAND_SURFCHAT_LOOKUP)

    val COMMAND_SURFCHAT_FUNCTIONALITY = create(ChatPermissions.COMMAND_SURFCHAT_FUNCTIONALITY)
    val COMMAND_SURFCHAT_FUNCTIONALITY_TOGGLE =
        create(ChatPermissions.COMMAND_SURFCHAT_FUNCTIONALITY_TOGGLE)
    val COMMAND_SURFCHAT_FUNCTIONALITY_STATUS =
        create(ChatPermissions.COMMAND_SURFCHAT_FUNCTIONALITY_STATUS)
    val COMMAND_SURFCHAT_FUNCTIONALITY_LIST =
        create(ChatPermissions.COMMAND_SURFCHAT_FUNCTIONALITY_LIST)

    val COMMAND_SURFCHAT_LOOKUP_HELP = create(ChatPermissions.COMMAND_SURFCHAT_LOOKUP_HELP)

    val COMMAND_IGNORE = create(ChatPermissions.COMMAND_IGNORE)
    val COMMAND_IGNORE_LIST = create(ChatPermissions.COMMAND_IGNORE_LIST)

    val COMMAND_DIRECT_SPY = create(ChatPermissions.COMMAND_DIRECT_SPY)
    val COMMAND_DIRECT_SPY_CLEAR = create(ChatPermissions.COMMAND_DIRECT_SPY_CLEAR)

    val COMMAND_TEAMCHAT = create(ChatPermissions.COMMAND_TEAMCHAT)

    val COMMAND_PM = create(ChatPermissions.COMMAND_PM)
    val COMMAND_REPLY = create(ChatPermissions.COMMAND_REPLY)
}
