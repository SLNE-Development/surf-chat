package dev.slne.surf.chat.api

import dev.slne.surf.surfapi.core.api.util.requiredService
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.UUID

interface SurfChatApi {
    /**
     * Logs a message for a specific player.
     *
     * @param player The UUID of the player.
     * @param message The message to log.
     */
    fun logMessage(player: UUID, message: Component)

    /**
     * Sends a text message with prefix to a specific player and logs it.
     *
     * @param player The player to send the message to.
     * @param message The message to send.
     */
    fun sendText(player: Player, message: Component)

    /**
     * Sends a text message without a prefix to a specific player and logs it.
     *
     * @param player The player to send the message to.
     * @param message The message to send.
     */
    fun sendRawText(player: Player, message: Component)

    companion object {
        val INSTANCE = requiredService<SurfChatApi>()
    }
}

val surfChatApi get() = SurfChatApi.INSTANCE