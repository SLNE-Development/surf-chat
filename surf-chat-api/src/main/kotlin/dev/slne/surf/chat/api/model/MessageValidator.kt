package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.type.MessageType
import dev.slne.surf.chat.api.type.MessageValidationResult
import net.kyori.adventure.text.Component

/**
 * Represents a model for validating and parsing chat messages.
 * This interface defines methods for validating the content of messages
 * and parsing them based on specific criteria.
 */
interface MessageValidator {

    /**
     * Validates a chat message.
     *
     * @param message The message content as a `Component`.
     * @param type The type of the chat message (e.g., global, private).
     * @param user The user who sent the message.
     * @return The result of the validation as a `MessageValidationResult`.
     */
    fun validate(message: Component, type: MessageType, user: ChatUser): MessageValidationResult

    /**
     * Parses a chat message and performs an action upon successful parsing.
     *
     * @param message The message content as a `Component`.
     * @param type The type of the chat message (e.g., global, private).
     * @param user The user who sent the message.
     * @param onSuccess A callback function to execute if parsing is successful.
     */
    fun parse(message: Component, type: MessageType, user: ChatUser, onSuccess: () -> Unit)
}