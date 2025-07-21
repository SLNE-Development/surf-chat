package dev.slne.surf.chat.bukkit.util

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.core.service.userService
import net.kyori.adventure.audience.Audience
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun User.player() = Bukkit.getPlayer(this.uuid)
fun Audience.user() = when (this) {
    is Player -> userService.getUser(this.uniqueId)
    else -> null
}