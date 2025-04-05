package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.type.MessageValidationError
import net.kyori.adventure.text.Component

interface MessageValidatorModel {
    fun validate(message: Component, type: ChatMessageType): MessageValidationError
}