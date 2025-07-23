package dev.slne.surf.chat.bukkit

import dev.slne.surf.chat.bukkit.command.settings.settingsCommand

object BukkitCommandManager {
    fun registerCommands() {
        settingsCommand()
    }
}