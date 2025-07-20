package dev.slne.surf.chat.api.model

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

interface MessageValidator {
    fun validate(message: String): Boolean
    fun validate(message: Component): Boolean = this.validate(PlainTextComponentSerializer.plainText().serialize(message))
}