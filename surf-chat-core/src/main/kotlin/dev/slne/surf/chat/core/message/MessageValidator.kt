package dev.slne.surf.chat.core.message

import dev.slne.surf.chat.api.entity.User
import net.kyori.adventure.text.Component

interface MessageValidator<T> {
    val message: T
    var failureMessage: Component
    fun validate(user: User): Boolean
}