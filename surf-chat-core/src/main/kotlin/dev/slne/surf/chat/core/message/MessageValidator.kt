package dev.slne.surf.chat.core.message

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.api.message.MessageValidationResult

interface MessageValidator<T> {
    val message: T
    fun validate(user: User): MessageValidationResult
}