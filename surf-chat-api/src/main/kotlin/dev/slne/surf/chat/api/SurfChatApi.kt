package dev.slne.surf.chat.api

import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.*

interface SurfChatApi {
    /**
     * Logs a message for a specific player.
     * After logging, the message can be deleted by chat interaction or command.
     *
     * @param player The UUID of the player.
     * @param type The type of chat message.
     * @param message The message to log.
     */
    suspend fun logMessage(player: UUID, type: ChatMessageType, message: Component, messageID: UUID)

    companion object {
        val INSTANCE = requiredService<SurfChatApi>()

        const val MESSAGING_CHANNEL_IDENTIFIER = "surf-chat:messaging"
        const val TEAM_CHAT_IDENTIFIER = "surf-chat:teamchat"
    }
}

val surfChatApi get() = SurfChatApi.INSTANCE