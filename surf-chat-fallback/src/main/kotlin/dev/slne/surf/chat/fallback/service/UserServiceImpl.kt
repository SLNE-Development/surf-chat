package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.entity.User
import dev.slne.surf.chat.core.service.UserService
import dev.slne.surf.chat.fallback.repository.userRepository
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(UserService::class)
class UserServiceImpl : UserService, Services.Fallback {
    override val onlineUsers = mutableObjectSetOf<User>()

    override fun findUserByUuid(uuid: UUID) = onlineUsers.find { it.uuid == uuid }
    override fun findUserByName(name: String) =
        onlineUsers.find { it.name.equals(name, ignoreCase = true) }

    override suspend fun loadUserByUuid(uuid: UUID) = userRepository.loadUserByUuid(uuid)
    override suspend fun loadUserByName(name: String) = userRepository.loadUserByName(name)

    override suspend fun saveUser(user: User) {
        userRepository.saveUser(user)
    }
}