package dev.slne.surf.chat.bukkit.util.utils

import dev.slne.surf.chat.api.model.ChatUserModel
import dev.slne.surf.chat.api.surfChatApi
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

suspend fun Player.toChatUser(): ChatUserModel {
    return databaseService.getUser(this.uniqueId)
}

fun ChatUserModel.sendText(text: Component) {
    val player = Bukkit.getPlayer(this.uuid) ?: return
    surfChatApi.sendText(player, text)
}

fun ChatUserModel.sendRawText(text: Component) {
    val player = Bukkit.getPlayer(this.uuid) ?: return
    surfChatApi.sendRawText(player, text)
}

fun ChatUserModel.toPlayer(): Player? {
    return Bukkit.getPlayer(this.uuid)
}

fun Component.toPlainText(): String {
    return PlainTextComponentSerializer.plainText().serialize(this)
}

suspend fun UUID.getUsername(): String {
    return PlayerLookupService.getUsername(this) ?: "Unknown"
}

fun player(userName: String): Player? {
    return Bukkit.getPlayer(userName)
}

fun player(uuid: UUID): Player? {
    return Bukkit.getPlayer(uuid)
}