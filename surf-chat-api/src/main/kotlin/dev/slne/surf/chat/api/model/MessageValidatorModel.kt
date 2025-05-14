package dev.slne.surf.chat.api.model

import dev.slne.surf.chat.api.type.ChatMessageType
import dev.slne.surf.chat.api.type.MessageValidationResult
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

/**
 * Represents a model for validating and parsing chat messages.
 * This interface defines methods for validating the content of messages
 * and parsing them based on specific criteria.
 */
interface MessageValidatorModel {

    /**
     * Validates a chat message.
     *
     * @param message The message content as a `Component`.
     * @param type The type of the chat message (e.g., global, private).
     * @param sender The player who sent the message.
     * @return The result of the validation as a `MessageValidationResult`.
     */
    fun validate(message: Component, type: ChatMessageType, sender: Player): MessageValidationResult

    /**
     * Parses a chat message and performs an action upon successful parsing.
     *
     * @param message The message content as a `Component`.
     * @param type The type of the chat message (e.g., global, private).
     * @param player The player who sent the message.
     * @param onSuccess A callback function to execute if parsing is successful.
     */
    fun parse(message: Component, type: ChatMessageType, player: Player, onSuccess: () -> Unit)
}