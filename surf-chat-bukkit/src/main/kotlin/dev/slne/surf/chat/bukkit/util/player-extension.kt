package dev.slne.surf.chat.bukkit.util

import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelMember
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.entry.IgnoreListEntry
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
import java.util.*

fun User.player() = Bukkit.getPlayer(this.uuid)
fun Audience.user() = when (this) {
    is Player -> userService.findUserByUuid(this.uniqueId)
    else -> null
}

fun User.channelMember(channel: Channel) = channel.members.find { it.uuid == this.uuid }

fun ChannelMember.player() = Bukkit.getPlayer(this.uuid)
fun ChannelMember.user() = userService.findUserByUuid(uuid)
fun Audience.isConsole() = this is ConsoleCommandSender

fun Audience.name() = when (this) {
    is Player -> this.name
    is ConsoleCommandSender -> "Console"
    else -> "Error"
}

fun User.hasPermission(permission: String) = player()?.hasPermission(permission) ?: false

fun CommandSender.realName() = when (this) {
    is Player -> this.name
    is ConsoleCommandSender -> "Console"
    is BlockCommandSender -> "Block"
    is CommandMinecart -> "CommandBlockMinecart"
    else -> "Error"
}

fun Audience.toUserOrNull() = when (this) {
    is Player -> userService.findUserByUuid(this.uniqueId)
    else -> null
}

fun Audience.toUserOrThrow() = when (this) {
    is Player -> userService.findUserByUuid(this.uniqueId)
        ?: error("User not found for player ${this.name}")

    else -> error("Audience is not a player")
}

fun User.ignore(name: String, uuid: UUID) {
    ignorelist.add(
        IgnoreListEntry(
            this.uuid,
            this.name,
            uuid,
            name,
            System.currentTimeMillis()
        )
    )
}

fun User.unignore(uuid: UUID) = ignorelist.removeIf { it.target == uuid }
fun User.ignores(uuid: UUID) = ignorelist.any { it.target == uuid }


fun User.sendText(block: SurfComponentBuilder.() -> Unit) = player()?.sendText { block() }