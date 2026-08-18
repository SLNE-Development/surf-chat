package dev.slne.surf.chat.core.client.util

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.nameOrNull
import dev.slne.surf.api.core.messages.builder.SurfComponentBuilder
import dev.slne.surf.chat.core.client.platform.ChatPlatform
import net.kyori.adventure.audience.Audience
import java.util.*

fun Audience.nameOrUnknown() = nameOrNull() ?: "#Unknown"

inline fun UUID.sendText(block: SurfComponentBuilder.() -> Unit) {
    ChatPlatform.sendMessage(this, buildText(block))
}

fun UUID.hasPermission(permission: String) = ChatPlatform.hasPermission(this, permission)
