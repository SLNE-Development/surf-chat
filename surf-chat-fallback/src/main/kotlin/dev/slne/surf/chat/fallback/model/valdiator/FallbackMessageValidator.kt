package dev.slne.surf.chat.fallback.model.valdiator

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.api.model.MessageValidator
import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.api.type.MessageValidationResult
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services

@AutoService(MessageValidator::class)
class FallbackMessageValidator : MessageValidator, Services.Fallback {
    override fun validate(
        message: Component,
        type: MessageType,
        user: ChatUser
    ): MessageValidationResult {
        TODO("Not yet implemented")
    }

    override fun parse(
        message: Component,
        type: MessageType,
        user: ChatUser,
        onSuccess: () -> Unit
    ) {
        TODO("Not yet implemented")
    }
}