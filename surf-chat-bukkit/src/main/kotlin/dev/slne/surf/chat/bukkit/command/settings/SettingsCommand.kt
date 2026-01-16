package dev.slne.surf.chat.bukkit.command.settings

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.dialog.settingsDialog
import dev.slne.surf.chat.bukkit.permission.PermissionRegistry
import dev.slne.surf.chat.bukkit.plugin

fun settingsCommand() = commandAPICommand("settings", plugin) {
    withPermission(PermissionRegistry.COMMAND_SETTINGS)
    settingsPingCommand()
    settingsDirectMessagesCommand()

    playerExecutor { player, _ ->
        plugin.launch {
            player.showDialog(settingsDialog(player.uniqueId))
        }
    }
}