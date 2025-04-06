package dev.slne.surf.chat.bukkit.model

import dev.slne.surf.chat.api.model.MessageValidatorModel
import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.type.MessageValidationError
import net.kyori.adventure.text.Component

class BukkitMessageValidator(): MessageValidatorModel {
    override fun validate(message: Component, type: ChatMessageType): MessageValidationError {
        return MessageValidationError.SUCCESS
    }
}