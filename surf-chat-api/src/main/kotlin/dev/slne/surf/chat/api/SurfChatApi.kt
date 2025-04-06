package dev.slne.surf.chat.api

import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.UUID

interface SurfChatApi {
    /**
     * Logs a message for a specific player.
     *
     * @param player The UUID of the player.
     * @param type The type of chat message.
     * @param message The message to log.
     */
    suspend fun logMessage(player: UUID, type: ChatMessageType, message: Component)

    /**
     * Sends a text message with prefix to a specific player and logs it.
     *
     * @param player The player to send the message to.
     * @param message The message to send.
     * @param uuid The UUID of the message.
     */
    fun sendText(player: Player, message: Component, uuid: UUID = UUID.randomUUID())

    /**
     * Sends a text message without a prefix to a specific player and logs it.
     *
     * @param player The player to send the message to.
     * @param message The message to send.
     * @param uuid The UUID of the message.
     */
    fun sendRawText(player: Player, message: Component, uuid: UUID = UUID.randomUUID())

    companion object {
        val INSTANCE = requiredService<SurfChatApi>()
    }
}

val surfChatApi get() = SurfChatApi.INSTANCE