package dev.slne.surf.chat.api.message

import dev.slne.surf.chat.api.entry.DenylistEntry
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component

/**
 * Represents the result of a message validation process.
 *
 * This sealed class is used to represent whether the validation of a message
 * has succeeded or failed. Subclasses represent specific outcomes of the validation.
 */
sealed class MessageValidationResult {
    /**
     * Represents a successful result of message validation.
     *
     * This class is a part of the `MessageValidationResult` hierarchy and indicates
     * that the message passed validation successfully without any errors.
     */
    class Success : MessageValidationResult()

    /**
     * Represents a failure result in the message validation process.
     *
     * This class is a specific type of `MessageValidationResult` that is used
     * to signify that a validation operation has resulted in an error. It holds
     * a reference to the specific `MessageValidationError` that describes the
     * cause of the failure.
     *
     * @property error The specific validation error that caused the failure.
     */
    data class Failure(val error: MessageValidationError) : MessageValidationResult()

    /**
     * Checks whether the current instance of `MessageValidationResult` represents a successful validation result.
     *
     * @return `true` if the current instance is of type `Success`, otherwise `false`.
     */
    fun isSuccess(): Boolean = this is Success

    /**
     * Determines if the current instance of `MessageValidationResult` represents a failure.
     *
     * @return `true` if the current instance is of type `Failure`, otherwise `false`.
     */
    fun isFailure(): Boolean = this is Failure

    /**
     * Retrieves the `MessageValidationError` associated with this instance of `MessageValidationResult`
     * if it is of type `Failure`.
     *
     * If the instance is not of type `Failure`, this method returns null.
     *
     * @return the `MessageValidationError` if the instance is `Failure`, or null otherwise.
     */
    fun getErrorOrNull(): MessageValidationError? = (this as? Failure)?.error

    /**
     * Retrieves the associated `MessageValidationError` if the current instance is of type `Failure`.
     * If the instance is not of type `Failure`, this method throws an exception.
     *
     * @return the `MessageValidationError` associated with this `MessageValidationResult`.
     * @throws IllegalStateException if the instance is not a `Failure` or has no error.
     */
    fun getErrorOrThrow(): MessageValidationError =
        (this as? Failure)?.error ?: error("No error found.")

    /**
     * Represents validation errors that can occur when processing a user message.
     *
     * This sealed class serves as the base for various specific message validation errors.
     * Each subclass defines a specific type of validation error encountered during the
     * validation process.
     *
     * @property errorMessage The error message associated with the validation error.
     * @property name A human-readable name or description for the validation error.
     */
    sealed class MessageValidationError(val errorMessage: Component, val name: String) {
        /**
         *
         */
        class EmptyContent :
            MessageValidationError(
                buildText { error("Deine Nachricht darf nicht leer sein.") },
                "Kein Inhalt"
            )

        /**
         *
         */
        data class DenylistedWord(val denylistEntry: DenylistEntry) :
            MessageValidationError(
                buildText { error("Bitte achte auf deine Wortwahl.") },
                "Unerlaubtes Wort: ${denylistEntry.word}"
            )

        /**
         * Represents a validation error indicating that a message contains an unauthorized link.
         *
         * This error is triggered when a message includes a link that is not permitted based on
         * the chat's validation rules. The `url` property holds the link determined to be disallowed.
         *
         * @property url The disallowed URL that caused the validation error.
         */
        data class BadLink(val url: String) :
            MessageValidationError(
                buildText { error("Deine Nachricht enthält einen unerlaubten Link.") },
                "Unerlaubter Link: $url"
            )

        /**
         *
         */
        data class BadCharacters(val chars: String) :
            MessageValidationError(
                buildText { error("Deine Nachricht enthält unerlaubte Zeichen.") },
                "Unerlaubte Zeichen: $chars"
            )

        /**
         * Represents a specific type of message validation error that occurs
         * when a user is sending messages too frequently, classified as spamming.
         *
         * @property next The timestamp indicating when the user can send the next message.
         * In milliseconds since epoch.
         *
         * This validation is triggered when the system detects that a user is attempting
         * to send messages within a short time frame, violating the allowed messaging rate.
         * The user will receive a descriptive error message instructing them to wait before
         * sending another message.
         */
        data class TooOften(val next: Long) :
            MessageValidationError(
                buildText { error("Bitte warte einen Moment, bevor du eine weitere Nachricht sendest.") },
                "Spam"
            )

        /**
         * Represents a validation error indicating that the user cannot send messages
         * due to the chat system being temporarily disabled for them.
         *
         * This error typically occurs when certain conditions, such as the presence
         * of too many players, lead to the system restricting message-sending capabilities.
         *
         * Inherits from [MessageValidationError] and provides specific error text
         * and identification for the "auto-disabled" condition.
         */
        class AutoDisabled :
            MessageValidationError(
                buildText { error("Du kannst zurzeit nicht schreiben.") },
                "Zu viele Spieler"
            )

        /**
         * Represents a specific type of `MessageValidationError` that occurs when the chat is temporarily disabled.
         *
         * This error is used to notify that the chat system has been disabled for certain users or conditions.
         * Typically, this can happen if the chat functionality is globally disabled or restricted
         * based on specific rules applied to the system or user permissions.
         *
         * @constructor Constructs the `ChatDisabled` error with a predefined error message and name.
         */
        class ChatDisabled :
            MessageValidationError(
                buildText { error("Der Chat ist vorübergehend deaktiviert.") },
                "Chat deaktiviert"
            )
    }
}