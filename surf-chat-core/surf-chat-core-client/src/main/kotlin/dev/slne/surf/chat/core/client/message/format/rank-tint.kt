package dev.slne.surf.chat.core.client.message.format

import dev.slne.surf.chat.core.client.hook.LuckPermsHook
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import java.util.*
import java.util.concurrent.ConcurrentHashMap

private val tintCache = ConcurrentHashMap<String, Optional<TextColor>>()

fun rankMessageTint(senderUuid: UUID): TextColor? = rankMessageTint(LuckPermsHook.getPrefix(senderUuid))

fun rankMessageTint(prefix: String): TextColor? = tintCache.computeIfAbsent(prefix) {
    val rankColor = runCatching { MiniMessage.miniMessage().deserialize(prefix) }
        .getOrNull()
        ?.let { findRankColor(it, null) }

    Optional.ofNullable(rankColor?.let { TextColor.lerp(0.40f, NamedTextColor.WHITE, it) })
}.orElse(null)

private fun findRankColor(component: Component, inherited: TextColor?): TextColor? {
    val color = component.color() ?: inherited

    if (component is TextComponent && component.content().isNotBlank()) {
        return color
    }

    return component.children().firstNotNullOfOrNull { findRankColor(it, color) }
}
