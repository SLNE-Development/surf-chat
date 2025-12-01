package dev.slne.surf.chat.api.message

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
    object Success : MessageValidationResult()

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
    data class Failure(val message: String, val sendTeamWarning: Boolean = true) :
        MessageValidationResult()

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

    fun getErrorOrThrow() = when (this) {
        is Success -> error("No error present in Success result")
        is Failure -> this.message to sendTeamWarning
    }
}