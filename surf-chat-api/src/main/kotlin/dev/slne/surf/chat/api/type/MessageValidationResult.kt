package dev.slne.surf.chat.api.type

/**
 * Represents the result of a message validation process.
 * This enum defines the possible outcomes when validating a chat message,
 * such as success or various failure reasons.
 */
enum class MessageValidationResult {
    /**
     * The message validation was successful.
     */
    SUCCESS,

    /**
     * The message validation failed because the sender is muted.
     */
    FAILED_MUTED,

    /**
     * The message validation failed due to spam detection.
     */
    FAILED_SPAM,

    /**
     * The message validation failed because the sender attempted to message themselves.
     */
    FAILED_SELF,

    /**
     * The message validation failed due to a bad or disallowed link.
     */
    FAILED_BAD_LINK,

    /**
     * The message validation failed because it contains prohibited words.
     */
    FAILED_BAD_WORD,

    /**
     * The message validation failed due to invalid or disallowed characters.
     */
    FAILED_BAD_CHARACTER,

    /**
     * The message validation failed because private messaging is disabled for the recipient.
     */
    FAILED_PM_DISABLED,

    /**
     * The message validation failed because the sender is on the recipient's blacklist.
     */
    FAILED_BLACKLIST,

    /**
     * The message validation failed because the recipient is ignoring the sender.
     */
    FAILED_IGNORING,
}