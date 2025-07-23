package dev.slne.surf.chat.bukkit

import dev.slne.surf.chat.bukkit.command.settings.settingsCommand
import dev.slne.surf.chat.bukkit.command.surfchat.surfChatCommand

object BukkitCommandManager {
    fun registerCommands() {
        settingsCommand()
        surfChatCommand()
    }
}