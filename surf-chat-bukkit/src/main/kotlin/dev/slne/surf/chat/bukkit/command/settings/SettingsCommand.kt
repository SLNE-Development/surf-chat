package dev.slne.surf.chat.bukkit.command.settings

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.chat.bukkit.dialog.settingsDialog
import dev.slne.surf.chat.bukkit.permission.SurfChatPermissionRegistry

fun settingsCommand() = commandAPICommand("settings") {
    withPermission(SurfChatPermissionRegistry.COMMAND_SETTINGS)
    settingsPingCommand()

    playerExecutor { player, _ ->
        player.showDialog(settingsDialog())
    }
}