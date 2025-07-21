package dev.slne.surf.chat.fallback.entity

import dev.slne.surf.chat.api.entity.ConfigurableUser
import dev.slne.surf.chat.api.entity.User
import org.bukkit.Bukkit
import java.util.*

data class FallbackUser(override val name: String, override val uuid: UUID) : User {
    override fun hasPermission(permission: String) =
        Bukkit.getPlayer(uuid)?.hasPermission(permission) ?: false

    override fun configure(): ConfigurableUser {
        return FallbackConfigurableUser(uuid)
    }
}