package dev.slne.surf.chat.bukkit.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object SurfChatPermissionRegistry : PermissionRegistry() {
    private const val PREFIX = "surf.chat"
    private const val PREFIX_COMMAND = "$PREFIX.command"

    val FILTER_BYPASS = create("$PREFIX.filter.bypass")
    val TEAM_ACCESS = create("$PREFIX.team")

    val COMMAND_SURFCHAT = create("$PREFIX_COMMAND.surfchat")
    val COMMAND_SURFCHAT_DELETE = create("$PREFIX_COMMAND.surfchat.delete")
    val COMMAND_SURFCHAT_TELEPORT = create("$PREFIX_COMMAND.surfchat.teleport")
    val COMMAND_SURFCHAT_LOOKUP = create("$PREFIX_COMMAND.surfchat.lookup")

    val COMMAND_SETTINGS = create("$PREFIX_COMMAND.settings")
    val COMMAND_SETTINGS_PING = create("$PREFIX_COMMAND.settings.ping")
    val COMMAND_SETTINGS_DIRECT_MESSAGES = create("$PREFIX_COMMAND.settings.direct-messages")

    val COMMAND_TEAMCHAT = create("$PREFIX_COMMAND.teamchat")
}