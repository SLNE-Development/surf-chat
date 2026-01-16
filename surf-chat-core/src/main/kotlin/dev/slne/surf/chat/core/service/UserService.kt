package dev.slne.surf.chat.core.service

import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

val userService = requiredService<UserService>()

/**
 * Defines the operations for managing user information, allowing retrieval of online
 * and offline users by unique identifiers or names.
 */
interface UserService {
    val onlineUsers: ObjectSet<User>
    fun findUserByUuid(uuid: UUID): User?
    fun findUserByName(name: String): User?

    suspend fun loadUserByUuid(uuid: UUID): User?
    suspend fun loadUserByName(name: String): User?

    suspend fun findOrLoadByName(name: String): User? {
        return findUserByName(name) ?: loadUserByName(name)
    }

    suspend fun saveUser(user: User)
}