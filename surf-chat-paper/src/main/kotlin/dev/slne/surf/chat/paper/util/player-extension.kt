package dev.slne.surf.chat.paper.util

import dev.slne.surf.api.core.messages.adventure.nameOrNull
import net.kyori.adventure.audience.Audience
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.minecart.CommandMinecart

fun Audience.isConsole() = this is ConsoleCommandSender

fun CommandSender.realName() = when (this) {
    is ConsoleCommandSender -> "Console"
    is BlockCommandSender -> "CommandBlock"
    is CommandMinecart -> "CommandBlockMinecart"
    else -> nameOrNull() ?: "#null"
}
