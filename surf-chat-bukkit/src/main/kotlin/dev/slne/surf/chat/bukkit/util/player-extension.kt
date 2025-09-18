package dev.slne.surf.chat.bukkit.util

import dev.slne.surf.chat.api.entity.ChannelMember
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.core.service.userService
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.audience.Audience
import org.bukkit.Bukkit
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.minecart.CommandMinecart

fun User.player() = Bukkit.getPlayer(this.uuid)
fun Audience.user() = when (this) {
    is Player -> userService.getUser(this.uniqueId)
    else -> null
}

fun ChannelMember.player() = Bukkit.getPlayer(this.uuid)
fun ChannelMember.user() = userService.getUser(uuid)
fun Audience.isConsole() = this is ConsoleCommandSender

fun Audience.name() = when (this) {
    is Player -> this.name
    is ConsoleCommandSender -> "Console"
    else -> "Error"
}

fun CommandSender.realName() = when (this) {
    is Player -> this.name
    is ConsoleCommandSender -> "Console"
    is BlockCommandSender -> "Block"
    is CommandMinecart -> "CommandBlockMinecart"
    else -> "Error"
}


fun User.sendText(block: SurfComponentBuilder.() -> Unit) = player()?.sendText { block() }
fun ChannelMember.sendText(block: SurfComponentBuilder.() -> Unit) = player()?.sendText { block() }