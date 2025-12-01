package dev.slne.surf.chat.core.common.message

import dev.slne.surf.chat.api.message.MessageValidationResult
import dev.slne.surf.cloud.api.common.player.CloudPlayer

/**
 * An interface for validating messages of type T.
 *
 * This interface defines the contract for validating user messages
 * and returning the result of the validation process.
 *
 * @param T The type of the message that will be validated.
 */
interface MessageValidator {
    /**
     * Represents the message being validated or processed.
     *
     * This property holds the data of the message, allowing it to be
     * examined, validated, or otherwise manipulated within the context
     * of the `MessageValidator` interface. The type of the message is
     * generic, making this property adaptable to various implementations
     * and use cases depending on the specific type of message being handled.
     */
    val messageData: MessageData

    /**
     * Validates the user's message according to specific rules defined by the implementing class.
     *
     * @param user The user whose message is being validated.
     * @return A result of type `MessageValidationResult`, indicating whether the validation was
     *         successful or detailing the specific error if validation failed.
     */
    fun validate(user: CloudPlayer): MessageValidationResult
}