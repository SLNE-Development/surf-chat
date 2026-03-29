package dev.slne.surf.chat.paper.util

import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.getPointer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.permission.PermissionChecker
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.minecart.CommandMinecart
import java.util.*

fun Audience.isConsole() = this is ConsoleCommandSender
fun Audience.nameOrNull() = getPointer(Identity.NAME)
fun Audience.nameOrUnknown() = nameOrNull() ?: "#Unknown"
fun Audience.name() = nameOrNull() ?: error("Audience does not provide name pointer")
fun Audience.uuidOrNull() = getPointer(Identity.UUID)
fun Audience.uuid() = uuidOrNull() ?: error("Audience does not provide uuid pointer")
fun Audience.hasPermission(permission: String) = getPointer(PermissionChecker.POINTER)?.test(permission) ?: false

fun CommandSender.realName() = when (this) {
    is ConsoleCommandSender -> "Console"
    is BlockCommandSender -> "CommandBlock"
    is CommandMinecart -> "CommandBlockMinecart"
    else -> nameOrNull() ?: "#null"
}

inline fun UUID.sendText(block: SurfComponentBuilder.() -> Unit) = server.getPlayer(this)?.sendText(block)
fun UUID.hasPermission(permission: String) = server.getPlayer(this)?.hasPermission(permission) ?: false