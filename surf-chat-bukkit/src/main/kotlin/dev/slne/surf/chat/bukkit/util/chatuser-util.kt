package dev.slne.surf.chat.bukkit.util

import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.core.service.databaseService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player

suspend fun Player.toChatUser(): ChatUser {
    return databaseService.getUser(this.uniqueId)
}

fun Component.plain() = PlainTextComponentSerializer.plainText().serialize(this)