package dev.slne.surf.chat.velocity.util

import com.velocitypowered.api.proxy.Player
import dev.slne.surf.chat.api.channel.Channel
import dev.slne.surf.chat.api.channel.ChannelMember
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.core.service.databaseService
import dev.slne.surf.chat.velocity.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.gradle.internal.declarativedsl.dom.resolution.resolutionContainer
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

val timeFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("dd.MM.yyyy, HH:mm:ss", Locale.GERMANY)
    .withZone(ZoneId.of("Europe/Berlin"))

fun Long.formatTime(): String {
    return timeFormatter.format(Instant.ofEpochMilli(this))
}

fun debug(message: Any) {
    println("[SurfChat-Debug] $message")
}

fun Component.toPlainText(): String {
    return PlainTextComponentSerializer.plainText().serialize(this)
}

fun ChatUser.getPlayer(): Player? {
    return plugin.proxy.getPlayer(this.uuid).getOrNull()
}

suspend fun Player.toChatUser(): ChatUser {
    return databaseService.getUser(this.uniqueId)
}

fun ChatUser.sendRawText(component: Component) {
    this.getPlayer()?.sendMessage(component)
}

fun ChatUser.getUsername(): String {
    return this.getPlayer()?.username ?: "Unknown"
}

suspend fun ChatUser.getUsernameAsync(): String {
    return PlayerLookupService.getUsername(this.uuid) ?: "Unknown"
}

suspend fun UUID.getUsername(): String {
    return PlayerLookupService.getUsername(this) ?: "Unknown"
}

fun ChatUser.toChannelMember(channel: Channel): ChannelMember? {
    return channel.members.find { it.uuid == this.uuid }
}

fun ChatUser.sendText(block: SurfComponentBuilder.() -> Unit) {
    this.getPlayer()?.sendMessage(buildText {
        appendPrefix()
        append {
            block
        }
    })
}