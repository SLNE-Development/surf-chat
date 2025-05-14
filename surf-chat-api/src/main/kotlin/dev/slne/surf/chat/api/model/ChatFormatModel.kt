package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.type.ChatMessageType
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import java.util.*

/**
 * Represents the model for formatting chat messages.
 * This interface defines methods for formatting messages, loading server-specific data,
 * and retrieving server information.
 */
interface ChatFormatModel {

    /**
     * Formats a chat message based on the provided parameters.
     *
     * @param rawMessage The raw message content as a `Component`.
     * @param sender The player who sent the message.
     * @param viewer The player viewing the message.
     * @param messageType The type of the chat message (e.g., global, private).
     * @param channel The channel in which the message is sent.
     * @param messageID The unique identifier of the message.
     * @param warn Whether to display warnings (e.g., for missing items).
     * @return The formatted message as a `Component`.
     */
    fun formatMessage(
        rawMessage: Component,
        sender: Player,
        viewer: Player,
        messageType: ChatMessageType,
        channel: String,
        messageID: UUID,
        warn: Boolean
    ): Component

    /**
     * Loads server-specific data required for chat formatting.
     * This method is typically used to initialize or update server-related settings.
     */
    fun loadServer()

    /**
     * Retrieves the name of the current server.
     *
     * @return The name of the server as a `String`.
     */
    fun getServer(): String
}