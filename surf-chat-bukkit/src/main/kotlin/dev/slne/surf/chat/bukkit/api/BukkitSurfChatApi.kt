package dev.slne.surf.chat.bukkit.api

import com.google.auto.service.AutoService
import dev.slne.surf.chat.api.SurfChatApi
import net.kyori.adventure.text.Component
import net.kyori.adventure.util.Services.Fallback
import java.util.*

@AutoService(SurfChatApi::class)
class BukkitSurfChatApi(): SurfChatApi, Fallback {
    override fun logMessage(player: UUID, message: Component) {
        TODO("Not yet implemented")
    }
}