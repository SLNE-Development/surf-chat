package dev.slne.surf.chat.bukkit.command.settings

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.plugin

fun settingsCommand() = commandAPICommand("settings", plugin) {
    withPermission(PermissionRegistry.COMMAND_SETTINGS)
    settingsPingCommand()
    settingsDirectMessagesCommand()
}