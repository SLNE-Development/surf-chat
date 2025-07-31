package dev.slne.surf.chat.bukkit

import dev.slne.surf.chat.bukkit.command.channel.channelAdminCommand
import dev.slne.surf.chat.bukkit.command.channel.channelCommand
import dev.slne.surf.chat.bukkit.command.denylist.denylistCommand
import dev.slne.surf.chat.bukkit.command.ignore.ignoreCommand
import dev.slne.surf.chat.bukkit.command.settings.settingsCommand
import dev.slne.surf.chat.bukkit.command.surfchat.surfChatCommand
import dev.slne.surf.chat.bukkit.command.teamchatCommand

object BukkitCommandManager {
    fun registerCommands() {
        settingsCommand()
        surfChatCommand()
        teamchatCommand()
        channelCommand()
        channelAdminCommand()
        denylistCommand()
        ignoreCommand()
    }
}