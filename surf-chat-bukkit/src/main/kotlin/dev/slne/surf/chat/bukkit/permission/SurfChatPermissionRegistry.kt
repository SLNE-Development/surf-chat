package dev.slne.surf.chat.bukkit.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object SurfChatPermissionRegistry : PermissionRegistry() {
    private const val PREFIX = "surf.chat"
    private const val PREFIX_COMMAND = "$PREFIX.command"

    val FILTER_BYPASS = create("$PREFIX.filter.bypass")
    val TEAM_ACCESS = create("$PREFIX.team")

    val COMMAND_SURFCHAT_DELETE = create("$PREFIX_COMMAND.surfchat.delete")
    val COMMAND_SURFCHAT_TELEPORT = create("$PREFIX_COMMAND.surfchat.teleport")

    val COMMAND_TEAMCHAT = create("$PREFIX_COMMAND.teamchat")
}