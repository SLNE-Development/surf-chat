package dev.slne.surf.chat.api

import net.kyori.adventure.text.Component
import java.util.UUID

interface SurfChatApi {
    fun logMessage(player: UUID, message: Component)
}