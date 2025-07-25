package dev.slne.surf.chat.bukkit

import dev.slne.surf.chat.bukkit.command.settings.settingsCommand
import dev.slne.surf.chat.bukkit.command.surfchat.surfChatCommand
import dev.slne.surf.chat.bukkit.command.teamchatCommand

object BukkitCommandManager {
    fun registerCommands() {
        settingsCommand()
        surfChatCommand()
        teamchatCommand()
    }
}