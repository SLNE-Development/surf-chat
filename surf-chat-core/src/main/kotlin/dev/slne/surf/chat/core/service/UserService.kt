package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.surfapi.core.api.util.requiredService
import java.util.*

interface UserService {
    fun getUser(uuid: UUID): User?
    fun getUser(name: String): User?

    fun getOfflineUser(uuid: UUID, name: String): User

    companion object {
        val INSTANCE = requiredService<UserService>()
    }
}

val userService get() = UserService.INSTANCE