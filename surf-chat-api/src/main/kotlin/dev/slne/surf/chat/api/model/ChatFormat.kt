package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.user.ChatUser
import dev.slne.surf.chat.api.type.MessageType
import net.kyori.adventure.text.Component
import java.util.*

/**
 * Represents the model for formatting chat messages.
 * This interface defines methods for formatting messages.
 */
interface ChatFormat {

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
    fun format(
        rawMessage: Component,
        sender: ChatUser,
        viewer: ChatUser,
        messageType: MessageType,
        channel: String,
        messageID: UUID,
        warn: Boolean
    ): Component
}