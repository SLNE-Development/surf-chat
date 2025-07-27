package dev.slne.surf.chat.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.chat.core.service.UserService
import dev.slne.surf.chat.fallback.entity.FallbackUser
import net.kyori.adventure.util.Services
import org.bukkit.Bukkit
import java.util.*

@AutoService(UserService::class)
class FallbackUserService : UserService, Services.Fallback {
    override fun getUser(uuid: UUID) = Bukkit.getPlayer(uuid)?.let { FallbackUser(it.name, uuid) }
    override fun getUser(name: String) =
        Bukkit.getPlayer(name)?.let { FallbackUser(name, it.uniqueId) }

    override fun getOfflineUser(
        uuid: UUID,
        name: String
    ) = FallbackUser(name, uuid)
}