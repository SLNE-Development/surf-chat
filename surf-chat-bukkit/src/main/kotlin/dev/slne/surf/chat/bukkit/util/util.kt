package dev.slne.surf.chat.bukkit.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.Cancellable

fun Cancellable.cancel() {
    isCancelled = true
}

fun Component.plainText(): String = PlainTextComponentSerializer.plainText().serialize(this)