package dev.slne.surf.chat.bukkit.util

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.core.service.userService
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun Player.user() = userService.getUser(this.uniqueId)
fun User.player() = Bukkit.getPlayer(this.uuid)