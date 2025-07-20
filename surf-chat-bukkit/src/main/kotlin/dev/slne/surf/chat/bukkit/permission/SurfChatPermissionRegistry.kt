package dev.slne.surf.chat.bukkit.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object SurfChatPermissionRegistry : PermissionRegistry() {
    private const val PREFIX = "surf.chat"
    private const val PREFIX_COMMAND = "$PREFIX.command"

    val FILTER_BYPASS = create("$PREFIX.filter.bypass")
}