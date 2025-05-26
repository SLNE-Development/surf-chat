package dev.slne.surf.chat.bukkit.util.utils

import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

suspend fun Player.toChatUser(): ChatUser {
    return databaseService.getUser(this.uniqueId)
}


fun ChatUser.sendRawText(text: Component) {
    val player = Bukkit.getPlayer(this.uuid) ?: return

    player.sendMessage(text)
}

inline fun ChatUser.sendPrefixed(builder: SurfComponentBuilder.() -> Unit) { sendPrefixed(SurfComponentBuilder(builder)) }
inline fun Player.sendPrefixed(builder: SurfComponentBuilder.() -> Unit) { sendPrefixed(SurfComponentBuilder(builder)) }

fun ChatUser.sendPrefixed(text: Component) = sendRawText(Colors.PREFIX.append { text })
fun Player.sendPrefixed(text: Component) = this.sendMessage { Colors.PREFIX.append { text } }

fun ChatUser.toPlayer(): Player? {
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