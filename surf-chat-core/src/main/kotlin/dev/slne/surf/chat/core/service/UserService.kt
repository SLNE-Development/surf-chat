package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.UUID

interface UserService {
    fun getUser(uuid: UUID): User?
    fun getUser(name: String): User?

    companion object {
        val INSTANCE = requiredService<UserService>()
    }
}

val userService get() = UserService.INSTANCE