package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.core.service.UserService
import dev.slne.surf.chat.fallback.repository.userRepository
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(UserService::class)
class UserServiceImpl : UserService, Services.Fallback {
    private val _users = mutableObject2ObjectMapOf<UUID, User>()

    override val onlineUsers get() = _users.values.toObjectSet()

    override fun findUserByUuid(uuid: UUID) = onlineUsers.find { it.uuid == uuid }
    override fun findUserByName(name: String) =
        onlineUsers.find { it.name.equals(name, ignoreCase = true) }

    override fun cacheUser(user: User) {
        _users[user.uuid] = user
    }

    override fun invalidateUser(userUuid: UUID) {
        _users.remove(userUuid)
    }

    override suspend fun loadUserOrCreateByUuid(
        uuid: UUID,
        name: String
    ): User = userRepository.loadUserOrCreateByUuid(uuid, name)

    override suspend fun loadUserByUuid(uuid: UUID) = userRepository.loadUserByUuid(uuid)
    override suspend fun loadUserByName(name: String) = userRepository.loadUserByName(name)

    override suspend fun saveUser(user: User) {
        userRepository.saveUser(user)
    }
}