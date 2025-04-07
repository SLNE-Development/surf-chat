package dev.slne.surf.chat.bukkit.util

import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.api.user.DisplayUser
import dev.slne.surf.chat.core.service.databaseService
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.util.*

suspend fun Player.toChatUser(): ChatUserModel {
    return databaseService.getUser(this.uniqueId)
}

fun Player.toDisplayUser(): DisplayUser {
    return DisplayUser(this.uniqueId, this.name)
}

fun Audience.toDisplayUser(): DisplayUser {
    return DisplayUser(UUID.randomUUID(), "Unknown")
}

fun ChatUserModel.sendText(text: Component) {
    val player = Bukkit.getPlayer(this.uuid) ?: return
    surfChatApi.sendText(player, text)
}

fun ChatUserModel.toPlayer(): Player? {
    return Bukkit.getPlayer(this.uuid)
}